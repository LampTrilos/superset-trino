<template>
  <q-select
    filled
    :label="t('ou', 0)"
    :option-value="(opt) => opt.id"
    :option-label="(opt) => formatOu(opt)"
    :options="ouOptions"
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
import {useCodeFormatter} from "src/utils/code-formatter";

const { t } = useI18n();

const { formatOu } = useCodeFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null;
    depUnit?: string | null;
    fetchData?: boolean;
    manage?: boolean;
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
const ouOptions = ref([]);
let firstLoad = true;
let ouSearch = ref(null);
const getOusOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<Ou>>(
    '/api/v1/ous?' +
      (props.manage ? '&manage=true' : '') +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (ouSearch.value != null && ouSearch.value !== ''
        ? '&name=' + ouSearch.value
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
    'getOusOptions',
    ouSearch,
    props.modelValue,
    props.depUnit,
    props.manage,
  ],
  queryFn: getOusOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    ouOptions.value = res.pages.flatMap((page) => page.data.map((ou) => ou));
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  ouOptions.value = data.pages.flatMap((page) => page.data.map((ou) => ou));
}

function onScroll({ to }) {
  const lastIndex = ouOptions.value.length - 1;
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
    ouSearch.value = needle;
  });
}

function getModelValueToString() {
  let modelValue = '';
  if (typeof props.modelValue === 'string') {
    modelValue = props.modelValue;
  } else if (Array.isArray(props.modelValue)) {
    if (typeof props.modelValue[0] === 'string') {
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
