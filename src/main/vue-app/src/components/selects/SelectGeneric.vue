<template>
  <!-- Beware! The ITems must be id, description for them to work -->
  <q-select
    filled
    v-model="valuesSelected"
    :option-value="(opt) => opt"
    :option-label="(opt) => opt.description"
    :options="devOptions.length > 0 ? devOptions : listOptions"
  />
</template>
<script setup lang="ts">
import {ref} from 'vue';
import axios from 'axios';
import { useInfiniteQuery } from '@tanstack/vue-query';
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
import {ChallengeChannel, Enum, SealingApplication, PageResponse} from 'src/services/models';

// const { formatOu } = useCodeFormatter();
//This is a 2-way model between parent and child
const valuesSelected = defineModel('valuesSelected', { required: true })

const props = withDefaults(
  defineProps<{
    //modelValue: string[] | null;
    //depUnit?: string | null;
    //The dynamic url to fetch the options from
    fetchUrl: string;
    //For dev purposes, a static list of options
    devOptions: [];
    fetchData?: boolean;
    manage?: boolean;
  }>(),
  {
    //modelValue: () => null,
    devOptions: () => [],
    fetchData: () => true,
  }
);

let firstLoad = true;
let challengeChannelsSearch = ref(null);

const getOptions = async ({ pageParam = 0 }) => {
  const response = await axios.get<PageResponse<ChallengeChannel>>(
    props.fetchUrl);
  return {
    //data: response.data.content,
    data: response.data,
    nextPage: pageParam + 1,
    hasNextPage:
      (response.data.index + 1) * response.data.size < response.data.total,
  };
};
//The list of options fetched
const listOptions = ref([]);
const { data, isFetching, fetchNextPage, isLoading } = useInfiniteQuery({
  queryKey: [
    'getOptions',
    challengeChannelsSearch,
    valuesSelected,
    //props.depUnit,
    props.manage,
  ],
  queryFn: getOptions,
  getNextPageParam: (lastPage) =>
    lastPage.hasNextPage ? lastPage.nextPage : undefined,
  onSettled: (res) => {
    listOptions.value = res.pages.flatMap((page) => page.data.map((channel) => channel));
    firstLoad = false;
  },
  enabled: props.fetchData,
});

if (data != undefined && data.pages != undefined) {
  listOptions.value = data.pages.flatMap((page) => page.data.map((channel) => channel));
}

// function onScroll({ to }) {
//   const lastIndex = valuesSelected.value.length - 1;
//   if (
//     isFetching.value !== true &&
//     isLoading.value !== true &&
//     to === lastIndex
//   ) {
//     fetchNextPage();
//   }
// }

function filterFn(val, update) {
  update(() => {
    const needle = val.toLowerCase();
    challengeChannelsSearch.value = needle;
  });
}

// function getModelValueToString() {
//   let modelValue = '';
//   if (typeof props.modelValue === 'string') {
//     valuesSelected = props.modelValue;
//   } else if (Array.isArray(props.modelValue)) {
//     if (typeof props.modelValue[0] === 'string') {
//       valuesSelected = props.modelValue;
//     } else {
//       valuesSelected = props.modelValue.map((obj) => obj.id);
//     }
//   } else {
//     valuesSelected = props.modelValue.id;
//   }
//   return valuesSelected.toString();
// }
</script>
