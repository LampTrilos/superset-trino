<template>
  <q-select
    filled
    :label="t('katastash')"
    :option-value="(opt) => opt.id"
    :option-label="(opt) => opt.description"
    :options="katastashOptions"
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
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { Ou, PageResponse } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null;
    fetchData?: boolean;
  }>(),
  {
    modelValue: () => null,
    fetchData: () => true,
  }
);
const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  emit('update:modelValue', value);
};
const katastashOptions = ref([]);
let firstLoad = true;
let katastashSearch = ref(null);
const getOusOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<Ou>>(
    '/api/v1/item-enums/katastash?' +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (katastashSearch.value != null && katastashSearch.value !== ''
        ? '&search=' + katastashSearch.value
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
  queryKey: [
    'getKatastash',
    katastashSearch,
    props.modelValue,
  ],
  queryFn: getOusOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    katastashOptions.value = res.pages.flatMap((page) => page.data.map((ou) => ou));
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  katastashOptions.value = data.pages.flatMap((page) => page.data.map((ou) => ou));
}

function onScroll({ to }) {
  const lastIndex = katastashOptions.value.length - 1;
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
    katastashSearch.value = needle;
  });
}

function getModelValueToString() {
  let modelValue = '';
  if (typeof props.modelValue === 'string' || typeof props.modelValue === 'number') {
    modelValue = props.modelValue;
  } else if (Array.isArray(props.modelValue)) {
    if (typeof props.modelValue[0] === 'string' || typeof props.modelValue[0] === 'number') {
      modelValue = props.modelValue;
    } else {
      modelValue = props.modelValue.map((obj) => obj.id);
    }
  } else {
    modelValue = props.modelValue.id;
  }
  return modelValue.toString();
}
</script>
