<template>
  <q-table
    :wrap-cells="true"
    flat
    bordered
    :columns="columns"
    :rows="data?.content"
    row-key="id"
    :rows-per-page-options="[10, 20, 50]"
    v-model:pagination="tablePagination"
    @request="onRequest"
    :loading="isFetching"
  >
    <template #body-cell-id="propsBody">
      <q-td :props="propsBody">
        <router-link
          class="text-primary"
          :to="{
            name: 'CompoundTransaction',
            params: { id: propsBody.row.ctransaction?.id },
            query: { type: propsBody.row.type?.id },
          }"
          >{{ propsBody.value }}</router-link
        >
      </q-td>
    </template>
  </q-table>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useDateFormatter } from 'src/utils/date-formatter';
import {
  CompoundTransaction,
  Item,
  ITransaction,
  PageResponse,
} from 'src/services/models';
import { ref } from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { getITransactionsByItemId } from 'src/services/transactions';

import { useNumberFormatter } from 'src/utils/number-formatter';
const { englishToGreekNumber } = useNumberFormatter();

const { formatDep, formatItemCodeFull, formatUser } = useCodeFormatter();

const { formatDateTimeLocale, formatDate } = useDateFormatter();
const { t } = useI18n();
const columns = [
  {
    name: 'id',
    required: true,
    label: t('udc_short'),
    align: 'left',
    field: (row: ITransaction) => row.udc,
  },
  {
    name: 'type',
    required: true,
    label: t('typeTransaction'),
    align: 'left',
    field: (row: ITransaction) => row.type?.description,
  },
  {
    name: 'itemCode',
    required: true,
    label: t('codeItem', 0),
    align: 'left',
    field: (row: ITransaction) =>
      '(' +
      formatItemCodeFull(row.itemCode) +
      ') ' +
      (row.itemCodeEntity?.name ?? ''),
    style: 'max-width: 400px',
    headerStyle: 'width: 400px',
  },
  {
    name: 'serialNumber',
    field: (row: ITransaction) => row.itemJson?.serialNumber,
    required: true,
    label: t('serialNumber', 0),
    align: 'left',
  },
  {
    name: 'amount',
    required: true,
    label: t('amount'),
    align: 'left',
    field: (row: ITransaction) => englishToGreekNumber(row.amount),
  },
  {
    name: 'from',
    required: true,
    label: t('from'),
    align: 'left',
    field: (row: CompoundTransaction) =>
      (row.fromAccountEntity
        ? '(' +
          formatDep(row.fromAccountEntity?.code) +
          ') ' +
          (row.fromAccountEntity?.name ?? '')
        : '') +
      (row.fromOu ? row.fromOu.name + ' - ' : '') +
      (row.fromUser ? formatUser(row.fromUser) : ''),
  },
  {
    name: 'to',
    required: true,
    label: t('to'),
    align: 'left',
    field: (row: CompoundTransaction) =>
      (row.toAccountEntity
        ? '(' +
          formatDep(row.toAccountEntity?.code) +
          ') ' +
          (row.toAccountEntity?.name ?? '')
        : '') +
      (row.toOu ? row.toOu.name + ' - ' : '') +
      (row.toUser ? formatUser(row.toUser) : ''),
  },
  {
    name: 'actualDate',
    required: true,
    label: t('date'),
    align: 'left',
    field: (row: ITransaction) => formatDate(row.actualDate),
  },
  {
    name: 'updatedTimestamp',
    required: true,
    label: t('upTmp'),
    align: 'left',
    field: (row: ITransaction) => formatDateTimeLocale(row.updatedTimestamp),
  },
];

const props = defineProps({
  itemId: {
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
  queryKey: ['getITransactionsByItemId', queryPagination, props.itemId],
  queryFn: () =>
    getITransactionsByItemId(
      queryPagination.value.size,
      queryPagination.value.index,
      props.itemId
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
