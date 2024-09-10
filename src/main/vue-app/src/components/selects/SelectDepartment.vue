<template>
  <q-select
    filled
    :option-value="(opt) => opt.id"
    :option-label="(opt) => '(' + formatDep(opt.code) + ') ' + (opt.name ?? '')"
    :options="departmentOptions"
    :loading="false"
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
import { ref } from 'vue';
import { DepUnit, PageResponse } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { Id } from 'src/services/types';

const { formatDep } = useCodeFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: object | null | string;
    type?: string;
    parentDepUnit?: Id;
    manage?: boolean;
    fetchData?: boolean;
  }>(),
  {
    modelValue: () => null,
    type: '',
    parentDepUnit: () => null,
    fetchData: () => true,
  }
);
const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  emit('update:modelValue', value);
};

let codeSearch = ref(null);
let firstLoad = true;
const getDepartmentsOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<DepUnit>>(
    '/api/v1/departments/' +
      props.type +
      '?' +
      (props.manage ? '&manage=true' : '') +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (codeSearch.value != null && codeSearch.value !== ''
        ? '&codeOrName=' + codeSearch.value
        : '') +
      (props.parentDepUnit != null
        ? '&parentDepUnit=' + props.parentDepUnit
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

const departmentOptions = ref([]);
const { data, fetchNextPage, isFetching, isLoading } = useInfiniteQuery({
  queryKey: [
    'getDepartmentsOptions' + props.type + (props.manage ? '&manage=true' : ''),
    codeSearch,
    props.modelValue,
    props.parentDepUnit,
  ],
  queryFn: getDepartmentsOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    departmentOptions.value = res.pages.flatMap((page) =>
      page.data.map((item) => item)
    );
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  departmentOptions.value = data.pages.flatMap((page) =>
    page.data.map((item) => item)
  );
}

function onScroll({ to }) {
  const lastIndex = departmentOptions.value.length - 1;
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
    codeSearch.value = needle;
    //refetch({ refetchPage: (page, index) => index === 0 })
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
