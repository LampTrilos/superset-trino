<template>
  <q-select
    filled
    :label="label"
    :option-value="(opt) => opt.id"
    :option-label="(opt) => opt[optionLabel]"
    :options="enumOptions"
    @virtual-scroll="onScroll"
    use-input
    input-debounce="500"
    @filter="filterFn"
    v-model="modelValueObject"
    @update:model-value="updateValue"
  >
  </q-select>
</template>

<script setup lang="ts">
import axios from 'axios';
import { ref } from 'vue';
import { ItemCode, Ou, PageResponse } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';
import util from 'src/utils/util';

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null | number;
    fetchData?: boolean;
    label: string;
    enumEndPoint: string;
    queryParams?: string;
    optionLabel?: string;
  }>(),
  {
    modelValue: () => null,
    fetchData: () => true,
    optionLabel: () => 'description',
  }
);
let modelValueObject = ref(null);
if (props.modelValue != null) {
  axios
    .get('/api/v1/' + props.enumEndPoint + '/' + props.modelValue)
    .then((res) => {
      modelValueObject.value = res.data;
    })
    .catch((error) => {
      console.log(error);
      console.log('Error auto login');
    });
}

const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  console.log('value updated ' + (value != null ? value.id : null));
  emit('update:modelValue', value != null ? value.id : null);
};
const enumOptions = ref([]);
let firstLoad = true;
let search = ref(null);
const getEnumsOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<Ou>>(
    '/api/v1/' +
      props.enumEndPoint +
      '?' +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (search.value != null && search.value !== ''
        ? '&search=' + search.value
        : '') +
      (props.queryParams != null && props.queryParams !== ''
        ? '&' + props.queryParams
        : '') +
      '&size=25&index=' +
      pageParam
  );
  return {
    data: response.data.content,
    nextPage: pageParam + 1,
    hasNextPage:
      (response.data.index + 1) * response.data.size < response.data.total,
  };
};

const { data, fetchNextPage, isFetching, isLoading } = useInfiniteQuery({
  queryKey: [props.enumEndPoint, props.queryParams, search, props.modelValue],
  queryFn: getEnumsOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    enumOptions.value = res.pages.flatMap((page) => page.data.map((ou) => ou));
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  enumOptions.value = data.pages.flatMap((page) => page.data.map((ou) => ou));
}

function onScroll({ to }) {
  const lastIndex = enumOptions.value.length - 1;
  if (
    isFetching.value !== true &&
    isLoading.value !== true &&
    to === lastIndex
  ) {
    fetchNextPage();
  }
}

function filterFn(val, update) {
  update(() => {
    const needle = val.toLowerCase();
    search.value = needle;
  });
}

function getModelValueToString() {
  let modelValue = '';
  if (
    typeof props.modelValue === 'string' ||
    typeof props.modelValue === 'number'
  ) {
    modelValue = props.modelValue;
  }
  return modelValue.toString();
}
</script>
