<template>
  <q-table
    :columns="columns"
    :rows="data?.content"
    row-key="id"
    :rows-per-page-options="[10, 20, 50]"
    v-model:pagination="tablePagination"
    @request="onRequest"
    :loading="isFetching"
  >
  </q-table>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useDateFormatter } from 'src/utils/date-formatter';
import { Item, PageResponse } from 'src/services/models';
import { ref } from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { getLogsTransactions } from 'src/services/transactions';
import { useCodeFormatter } from 'src/utils/code-formatter';

const { formatDateTimeLocale } = useDateFormatter();
const { formatUser } = useCodeFormatter();
const { t } = useI18n();
const columns = [
  {
    name: 'user',
    required: true,
    label: t('user', 0),
    align: 'left',
    field: (row) => (row.user ? formatUser(row.user) : ''),
  },
  {
    name: 'action',
    required: true,
    label: t('action', 0),
    align: 'left',
    field: (row) => row.action.description,
  },
  {
    name: 'createdTimestamp',
    required: true,
    label: t('date'),
    align: 'left',
    field: (row) => formatDateTimeLocale(row.createdTimestamp),
  },
];

const props = defineProps({
  transactionId: {
    type: String,
    required: false,
    default: null,
  },
});

let tablePagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
});

let queryPagination = ref({
  size: tablePagination.value.rowsPerPage,
  index: tablePagination.value.page - 1,
});

const { data, isFetching } = useQuery({
  queryKey: ['getLogsTransactions', queryPagination, props.transactionId],
  queryFn: () =>
    getLogsTransactions(
      queryPagination.value.size,
      queryPagination.value.index,
      props.transactionId
    ),
  onSuccess: (data: PageResponse<Item>) => {
    tablePagination.value.page = data.index + 1;
    tablePagination.value.rowsPerPage = data.size !== -1 ? data.size : 0;
    tablePagination.value.rowsNumber = data.total;
  },
  initialData: () => {
    return {
      content: [],
      contentSize: 0,
      total: 0,
      index: 0,
      size: 0,
    };
  },
});

function onRequest(props) {
  queryPagination.value.size = props.pagination?.rowsPerPage || -1;
  queryPagination.value.index = props.pagination?.page - 1;
}
</script>
