<template>
  <q-select
    filled
    :label="t('item', 0)"
    :option-value="(opt) => opt.id"
    :option-label="(opt) => opt.serialNumber"
    :options="itemOptions"
    @virtual-scroll="onScroll"
    use-input
    input-debounce="500"
    @filter="filterFn"
    :model-value="modelValue"
    @update:model-value="updateValue"
    use-chips
  >
  </q-select>
</template>

<script setup lang="ts">
import axios from 'axios';
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { Item, PageResponse } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';
import { Id } from 'src/services/types';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null;
    itemCode?: Id;
    depUnit?: Id;
    assignedToDepUnit?: boolean | null;
    assigneeUser?: Id;
    assignedToUser?: boolean | null;
    assigneeOu?: Id;
    manage?: boolean | null;
    assignedToOu?: boolean | null;
    fetchData?: boolean;
    temporalReserved?: boolean | null;
    inTransaction?: boolean | null;
    assignedToTopikosYpeythynos?: boolean | null;
  }>(),
  {
    modelValue: () => null,
    itemCode: null,
    depUnit: null,
    assignedToDepUnit: null,
    assigneeUser: null,
    assignedToUser: null,
    assigneeOu: null,
    manage: null,
    temporalReserved: null,
    inTransaction: null,
    fetchData: () => true,
  }
);
const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  emit('update:modelValue', value);
};
const itemOptions = ref([]);
let firstLoad = true;
let codeSearch = ref(null);
const getItemsOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<Item>>(
    '/api/v1/items?' +
      (props.itemCode != null ? '&itemCode=' + props.itemCode : '') +
      (props.depUnit != null ? '&depUnit=' + props.depUnit : '') +
      (props.assignedToDepUnit != null
        ? '&assignedToDepUnit=' + props.assignedToDepUnit
        : '') +
      (props.assigneeUser != null
        ? '&assigneeUser=' + props.assigneeUser
        : '') +
      (props.assignedToUser != null
        ? '&assignedToUser=' + props.assignedToUser
        : '') +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (codeSearch.value != null && codeSearch.value !== ''
        ? '&serialNumber=' + codeSearch.value
        : '') +
      (props.assigneeOu != null ? '&assigneeOu=' + props.assigneeOu : '') +
      (props.manage != null ? '&manage=' + props.manage : '') +
      (props.temporalReserved != null
        ? '&temporalReserved=' + props.temporalReserved
        : '') +
      (props.inTransaction != null
        ? '&inTransaction=' + props.inTransaction
        : '') +
      (props.assignedToOu != null
        ? '&assignedToOu=' + props.assignedToOu
        : '') +
    (props.assignedToTopikosYpeythynos != null
      ? '&assignedToTopikosYpeythynos=' + props.assignedToTopikosYpeythynos
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
    'getItemsOptions',
    codeSearch,
    props.modelValue,
    props.itemCode,
    props.depUnit,
    props.assignedToDepUnit,
    props.assigneeUser,
    props.assignedToUser,
    props.manage,
    props.inTransaction,
    props.temporalReserved,
  ],
  queryFn: getItemsOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    itemOptions.value = res.pages.flatMap((page) =>
      page.data.map((item) => item)
    );
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  itemOptions.value = data.pages.flatMap((page) =>
    page.data.map((item) => item)
  );
}

function onScroll({ to }) {
  const lastIndex = itemOptions.value.length - 1;
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
