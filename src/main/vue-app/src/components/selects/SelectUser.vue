<template>
  <q-select
    filled
    :label="t('user', 0)"
    :option-value="(opt) => opt.id"
    :option-label="(opt) => formatUser(opt)"
    :options="userOptions"
    @virtual-scroll="onScroll"
    use-input
    input-debounce="500"
    @filter="filterFn"
    :model-value="modelValue"
    @update:model-value="updateValue"
    :loading="isFetching"
    hint="Πληκτρολογήστε ολόκληρο το μητρώο υπαλλήλου"
    hide-hint
  >
  </q-select>
</template>

<script setup lang="ts">
import axios from 'axios';
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { User, PageResponse } from 'src/services/models';
import { useInfiniteQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';

const { t } = useI18n();

const { formatUser } = useCodeFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: object | string | null;
    depUnit?: string | null;
    fetchData?: boolean;
  }>(),
  {
    modelValue: () => null,
    depUnit: () => null,
    fetchData: () => true,
  }
);
const emit = defineEmits(['update:modelValue']);
const updateValue = (value) => {
  emit('update:modelValue', value);
};
const userOptions = ref([]);
let firstLoad = true;
let userSearch = ref(null);
const getUsersOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<User>>(
    '/api/v1/users?' +
      (props.modelValue != null && firstLoad
        ? '&id=' + getModelValueToString()
        : '') +
      (userSearch.value != null && userSearch.value !== ''
        ? '&name=' + userSearch.value
        : '') +
      (props.depUnit != null && props.depUnit !== ''
        ? '&depUnit=' + props.depUnit
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
  queryKey: ['getUsersOptions', userSearch, props.modelValue, props.depUnit],
  queryFn: getUsersOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    userOptions.value = res.pages.flatMap((page) =>
      page.data.map((user) => user)
    );
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  userOptions.value = data.pages.flatMap((page) =>
    page.data.map((user) => user)
  );
}

function onScroll({ to }) {
  const lastIndex = userOptions.value.length - 1;
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
    userSearch.value = needle;
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
