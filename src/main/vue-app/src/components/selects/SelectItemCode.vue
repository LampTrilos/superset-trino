<template>
  <q-select
    filled
    :label="t('codeItem', 0)"
    :option-value="(opt) => opt.id"
    :option-label="
      (opt) => '(' + formatItemCodeFull(opt.code) + ') ' + (opt.name ?? '')
    "
    :options="itemCodeOptions"
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
import { ItemCode } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';

const { t } = useI18n();
const { formatItemCodeFull } = useCodeFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null;
    fetchData?: boolean;
    discreet?: boolean | null;
    inactive?: boolean | null;
  }>(),
  {
    modelValue: () => null,
    fetchData: () => true,
    discreet: () => null,
    inactive: () => null,
  }
);
const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  emit('update:modelValue', value);
};
const itemCodeOptions = ref([]);
let firstLoad = true;
let codeSearch = ref(null);
const getItemCodesOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<ItemCode>>(
    '/api/v1/codes?' +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (codeSearch.value != null && codeSearch.value !== ''
        ? '&codeOrName=' + codeSearch.value
        : '') +
      (props.discreet != null ? '&discreet=' + props.discreet : '') +
      (props.inactive != null ? '&inactive=' + props.inactive : '') +
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
  queryKey: ['getItemCodesOptions', codeSearch, props.modelValue, props.discreet, props.inactive],
  queryFn: getItemCodesOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    itemCodeOptions.value = res.pages.flatMap((page) =>
      page.data.map((item) => item)
    );
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  itemCodeOptions.value = data.pages.flatMap((page) =>
    page.data.map((item) => item)
  );
}

function onScroll({ to }) {
  const lastIndex = itemCodeOptions.value.length - 1;
  if (
    isFetching.value !== true &&
    isLoading.value !== true &&
    to === lastIndex
  ) {
    console.log('fetchNextPage');
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
