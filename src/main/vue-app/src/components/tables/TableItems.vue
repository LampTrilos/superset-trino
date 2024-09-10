<template>
  <q-table
    style="min-height: 675px"
    :columns="columns"
    :rows="data?.content"
    row-key="id"
    :rows-per-page-options="[10, 20, 50, 100]"
    v-model:pagination="tablePagination"
    @request="onRequest"
    :loading="isFetching"
    :selection="selectionEnabled ? 'multiple' : 'none'"
    v-model:selected="selected"
    :wrap-cells="true"
  >
    <template #top-right>
      <q-input
        borderless
        dense
        debounce="500"
        v-model="serialNumber"
        :placeholder="t('serialNumber')"
        clearable
      >
        <template #append>
          <q-icon name="search" />
        </template>
      </q-input>
    </template>
    <template #body-cell-serialNumber="propsBody">
      <q-td :props="propsBody">
        <router-link
          class="text-primary"
          :to="{
            name: myItems ? 'MyItemView' : 'ItemView',
            params: { id: propsBody.row.id },
          }"
          >{{ propsBody.value }}</router-link
        >
      </q-td>
    </template>
    <template #body-cell-actions="propsBody">
      <q-td :props="propsBody">
        <q-btn
          round
          flat
          size="sm"
          icon="sym_o_edit"
          color="primary"
          :to="{
            name: myItems ? 'MyItemView' : 'ItemView',
            params: { id: propsBody.row.id },
          }"
          ><q-tooltip>{{ t('edit') }}</q-tooltip></q-btn
        >
      </q-td>
    </template>
  </q-table>
  <div v-if="selectionEnabled" class="q-mt-md" >
    {{
      selected.length == 0
        ? 'Δεν έχετε επιλέξει υλικά'
        : 'Επιλεγμένα υλικά: ' +
          JSON.stringify(selected.map((sl) => sl.serialNumber), null, 1)
    }}
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useDateFormatter } from 'src/utils/date-formatter';
import { Item, PageResponse } from 'src/services/models';
import { ref, watch } from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { getItems } from 'src/services/items';
import { useCodeFormatter } from 'src/utils/code-formatter';
const { formatItemCodeCategory, formatItemCode, formatDep, formatUser } =
  useCodeFormatter();

const { formatDateTimeLocale } = useDateFormatter();
const { t } = useI18n();
const columns = [
  /*  {
    name: 'id',
    field: 'id',
    label: t('id'),
    required: true,
    align: 'left',
  },*/
  {
    name: 'code',
    required: true,
    label: t('codeItem', 0),
    align: 'left',
    field: (row: Item) =>
      '(' +
      formatItemCodeCategory(row.itemCode.code?.split(' ')[0]) +
      ' ' +
      formatItemCode(row.itemCode.code?.split(' ')[1]) +
      ') ' +
      (row.itemCode.name ?? ''),
    style: 'max-width: 400px',
    headerStyle: 'width: 400px',
  },
  {
    name: 'serialNumber',
    field: 'serialNumber',
    required: true,
    label: t('serialNumber', 0),
    align: 'left',
  },
  {
    name: 'department',
    label: t('department', 0),
    align: 'left',
    field: (row: Item) =>
      row.assigneeDepUnit
        ? '(' +
          formatDep(row.assigneeDepUnit?.code) +
          ') ' +
          (row.assigneeDepUnit?.name ?? '')
        : '',
    serverSideSorting: false,
  },
  {
    name: 'user',
    label: t('user', 0),
    align: 'left',
    field: (row: Item) =>
      (row.assigneeOu ? row.assigneeOu.name + ' - ' : '') +
      (row.assigneeUser ? formatUser(row.assigneeUser) : '') +
      (row.assignedToTopikosYpeythynos
        ? ' (' + t('topikos-ypeythynos') + ')'
        : ''),
    serverSideSorting: false,
  },
  {
    name: 'katastash',
    label: t('katastash', 0),
    align: 'left',
    field: (row: Item) =>
      (row.deleted ? t('statusDeleted') + ' ' : '') +
      (row.katastash ? row.katastash.description + ' ' : '') +
      (row.inTransaction ? t('inTransaction', 1) + ' ' : '') +
      (row.temporalReserved ? t('temporalReserved', 1) + ' ' : ''),
    serverSideSorting: false,
  },
  {
    name: 'createdTimestamp',
    required: true,
    label: t('crTmp'),
    align: 'left',
    field: (row: Item) => formatDateTimeLocale(row.createdTimestamp),
  },
  {
    name: 'updatedTimestamp',
    required: true,
    label: t('upTmp'),
    align: 'left',
    field: (row: Item) => formatDateTimeLocale(row.updatedTimestamp),
  },
  { name: 'actions' },
];

const props = defineProps({
  itemCode: {
    type: Object,
    required: false,
    default: null,
  },
  depUnit: {
    type: [Number, String],
    required: false,
    default: null,
  },
  assigneeUser: {
    type: [Number, String],
    required: false,
    default: null,
  },
  assigneeOu: {
    type: [Number, String],
    required: false,
    default: null,
  },
  assignedToDepUnit: {
    type: Boolean,
    required: false,
    default: null,
  },
  assignedToOu: {
    type: Boolean,
    default: null,
  },
  assignedToUser: {
    type: Boolean,
    required: false,
    default: null,
  },
  assignedToTopikosYpeythynos: {
    type: Boolean,
    required: false,
    default: null,
  },
  includeChildrenUnits: {
    type: Boolean,
    required: false,
    default: null,
  },
  selectionEnabled: {
    type: Boolean,
    required: false,
    default: false,
  },
  initialSelected: {
    type: Array,
    required: false,
    default: () => [],
  },
  modelValue: {
    type: Array,
    required: false,
    default: () => [],
  },
  myItems: {
    type: Boolean,
    required: false,
    default: false,
  },
  manage: {
    type: Boolean,
    required: false,
    default: null,
  },
  temporalReserved: {
    type: Boolean,
    required: false,
    default: null,
  },
  inTransaction: {
    type: Boolean,
    required: false,
    default: null,
  },
  inTransactionOrTemporalReserved: {
    type: Boolean,
    required: false,
    default: null,
  },
});

let serialNumber = ref(null);

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
  queryKey: [
    'getItems',
    queryPagination,
    props.itemCode?.id,
    serialNumber,
    props.depUnit,
    props.assigneeUser,
    props.assignedToDepUnit,
    props.assignedToUser,
    true,
    props.myItems,
    props.assigneeOu,
    props.assignedToTopikosYpeythynos,
    props.manage,
    props.assignedToOu,
    props.inTransaction,
    props.temporalReserved,
    props.inTransactionOrTemporalReserved,
  ],
  queryFn: () =>
    getItems(
      queryPagination.value.size,
      queryPagination.value.index,
      props.itemCode?.id,
      serialNumber.value,
      props.depUnit,
      props.assigneeUser,
      props.assignedToDepUnit,
      props.assignedToUser,
      props.includeChildrenUnits,
      props.myItems,
      props.assigneeOu,
      props.assignedToTopikosYpeythynos,
      props.manage,
      props.assignedToOu,
      props.inTransaction,
      props.temporalReserved,
      props.inTransactionOrTemporalReserved
    ),
  onSuccess: (data: PageResponse<Item>) => {
    tablePagination.value.page = data.index + 1;
    tablePagination.value.rowsPerPage = data.size !== -1 ? data.size : 0;
    tablePagination.value.rowsNumber = data.total;
  },
  enabled: props.itemCode.discreet,
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

let selected = ref(props.initialSelected);

const emit = defineEmits(['update:modelValue']);
watch(
  () => selected,
  () => {
    emit('update:modelValue', selected.value);
  },
  { deep: true }
);
</script>
