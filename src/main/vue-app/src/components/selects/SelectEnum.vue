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
    :model-value="modelValue"
    @update:model-value="updateValue"
  >
  </q-select>
</template>

<script setup lang="ts">
import axios from 'axios';
import {isProxy, ref, toRaw} from 'vue';
import { Ou, PageResponse } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null;
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
const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  emit('update:modelValue', value);
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
  let modelValue;
  let modelValueProps;
  /*  if (isProxy(props.modelValue)){
      modelValueProps = toRaw(props.modelValue);
    } else {
      modelValueProps = props.modelValue;
    }*/
  modelValueProps = props.modelValue;
  if (typeof modelValueProps === 'string' || typeof modelValueProps[0] === 'number') {
    modelValue = modelValueProps;
  } else if (Array.isArray(modelValueProps)) {
    if (typeof modelValueProps[0] === 'string' || typeof modelValueProps[0] === 'number') {
      modelValue = modelValueProps;
    }else {
      modelValue = modelValueProps.map((obj) => obj.id);
    }
  } else {
    modelValue = modelValueProps.id;
  }
  return modelValue.toString();
}
</script>
