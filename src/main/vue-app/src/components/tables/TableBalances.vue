<template>
  <q-table
    :columns="columns"
    :rows="data?.content"
    row-key="id"
    :rows-per-page-options="[10, 20, 50]"
    v-model:pagination="tablePagination"
    @request="onRequest"
    :loading="isFetching"
    :wrap-cells="true"
  >
    <template #body-cell-balance="propsBody">
      <q-td :props="propsBody">
        <span
          class="text-primary cursor-pointer"
          v-if="itemCode.discreet && propsBody.row.balance"
          @click="viewBalanceHandler(propsBody.row)"
          >{{ englishToGreekNumber(propsBody.row.balance) }}</span
        >
        <span v-else>{{ englishToGreekNumber(propsBody.row.balance) }}</span>
      </q-td>
    </template>
    <template #body-cell-availableBalance="propsBody">
      <q-td :props="propsBody">
        <span
          class="text-primary cursor-pointer"
          v-if="itemCode.discreet && propsBody.row.availableBalance"
          @click="viewBalanceAvailableHandler(propsBody.row)"
          >{{ englishToGreekNumber(propsBody.row.availableBalance) }}</span
        >
        <span v-else>{{
          englishToGreekNumber(propsBody.row.availableBalance)
        }}</span>
      </q-td>
    </template>
    <template #body-cell-reservedBalance="propsBody">
      <q-td :props="propsBody">
        <span
          class="text-primary cursor-pointer"
          v-if="
            itemCode.discreet &&
            propsBody.row.transactionsBalance +
              propsBody.row.temporalReservedBalance
          "
          @click="viewBalanceReservedHandler(propsBody.row)"
          >{{
            englishToGreekNumber(
              propsBody.row.transactionsBalance +
                propsBody.row.temporalReservedBalance
            )
          }}</span
        >
        <span v-else>{{
          englishToGreekNumber(
            propsBody.row.transactionsBalance +
              propsBody.row.temporalReservedBalance
          )
        }}</span>
      </q-td>
    </template>
    <template #body-cell-assignedBalance="propsBody">
      <q-td :props="propsBody">
        <span v-if="propsBody.row.user"></span>
        <span
          v-else-if="
            propsBody.row.totalBalance - propsBody.row.balance &&
            (!propsBody.row.user || (propsBody.row.user && itemCode.discreet))
          "
          class="text-primary cursor-pointer"
          @click="viewAssignedBalanceHandler(propsBody.row)"
          >{{
            englishToGreekNumber(
              subtractDecimals(
                propsBody.row.totalBalance,
                propsBody.row.balance
              )
            )
          }}</span
        >
        <span v-else>{{
          englishToGreekNumber(
            subtractDecimals(propsBody.row.totalBalance, propsBody.row.balance)
          )
        }}</span>
      </q-td>
    </template>
  </q-table>
  <DialogItems
    v-model="viewDialogItems"
    :assigned-to-user="assignedToUser"
    :assigned-to-dep-unit="assignedToDepUnit"
    :item-code="itemCode"
    :dep-unit="depUnit"
    :assignee-user="assigneeUser"
    :assignee-ou="assigneeOu"
    :assigned-to-ou="assignedToOu"
    :temporal-reserved="temporalReserved"
    :in-transaction="inTransaction"
    :in-transaction-or-temporal-reserved="inTransactionOrTemporalReserved"
  />
  <DialogBalances
    v-model="viewDialogDepartments"
    :assigned-to-user="assignedToUser"
    :assigned-to-dep-unit="true"
    :item-code="itemCode"
    :dep-unit="depUnit"
    :assignee-user="null"
    :type="'users-per-depunit'"
  />
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import {
  AccountBalance,
  ItemCode,
  OuAccountBalance,
  PageResponse,
  ParAccountBalance,
} from 'src/services/models';
import { ref } from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { getAccountBalances } from 'src/services/balances';
import DialogItems from 'components/dialog/DialogItems.vue';
import { Id } from 'src/services/types';
import DialogBalances from 'components/dialog/DialogBalances.vue';
const { formatDep, formatItemCodeCategory, formatItemCode, formatUser } =
  useCodeFormatter();
import { useNumberFormatter } from 'src/utils/number-formatter';
const { englishToGreekNumber, subtractDecimals } = useNumberFormatter();

const { t } = useI18n();
let columns = [];
if (props.type == 'partials') {
  columns = [
    {
      name: 'code',
      required: true,
      label: t('codeItem', 0),
      align: 'left',
      field: (row: ParAccountBalance) =>
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
      name: 'name',
      required: true,
      label: t('parDepUnit', 0),
      align: 'left',
      field: (row: ParAccountBalance) =>
        '(' +
        formatDep(row.parDepUnit.code) +
        ') ' +
        (row.parDepUnit.name ?? ''),
    },
    {
      name: 'balance',
      required: true,
      label: t('balancePar', 0),
      align: 'left',
      field: (row: ParAccountBalance) => englishToGreekNumber(row.balance),
      serverSideSorting: false,
    },
    {
      name: 'assignedBalance',
      required: true,
      label: t('assigned'),
      field: (row: ParAccountBalance) =>
        englishToGreekNumber(row.assignedBalance),
      align: 'left',
    },
    {
      name: 'totalBalance',
      required: true,
      label: t('totalBalance', 0),
      align: 'left',
      field: (row: ParAccountBalance) => englishToGreekNumber(row.totalBalance),
      serverSideSorting: false,
    },
  ];
} else if (props.type == 'under-gendepunit') {
  columns = [
    {
      name: 'code',
      required: true,
      label: t('codeItem', 0),
      align: 'left',
      field: (row: ParAccountBalance) =>
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
      name: 'name',
      required: true,
      label: t('department', 0),
      align: 'left',
      field: (row: AccountBalance) =>
        '(' + formatDep(row.depUnit.code) + ') ' + (row.depUnit.name ?? ''),
    },
    {
      name: 'assigned',
      required: true,
      label: t('user', 0),
      align: 'left',
      field: (row) =>
        (row.ou ? row.ou.name + (row.user ? ' - ' : '') : '') +
        (row.user
          ? formatUser(row.user) +
            (row.manager ? ' (' + t('topikos-ypeythynos') + ')' : '')
          : ''),
    },
    {
      name: 'balance',
      required: true,
      label: t('balance', 0),
      align: 'left',
      field: (row: AccountBalance) => englishToGreekNumber(row.balance),
      serverSideSorting: false,
    },
    {
      name: 'availableBalance',
      required: true,
      label: t('availableBalance', 0),
      align: 'left',
      field: (row: AccountBalance) =>
        englishToGreekNumber(row.availableBalance),
      serverSideSorting: false,
    },
    {
      name: 'reservedBalance',
      required: true,
      label: t('reservedBalance', 0),
      align: 'left',
      field: (row: AccountBalance) => englishToGreekNumber(row.reservedBalance),
      serverSideSorting: false,
    },
    {
      name: 'assignedBalance',
      required: true,
      label: t('assigned'),
      field: (row: AccountBalance) => englishToGreekNumber(row.assignedBalance),
      align: 'left',
    },
    {
      name: 'totalBalance',
      required: true,
      label: t('totalBalance', 0),
      align: 'left',
      field: (row: AccountBalance) => englishToGreekNumber(row.totalBalance),
      serverSideSorting: false,
    },
  ];
} else if (props.type == 'users') {
  columns = [
    {
      name: 'code',
      required: true,
      label: t('codeItem', 0),
      align: 'left',
      field: (row) =>
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
      name: 'name',
      required: true,
      label: t('department', 0),
      align: 'left',
      field: (row) =>
        '(' + formatDep(row.depUnit.code) + ') ' + (row.depUnit.name ?? ''),
    },
    {
      name: 'assigned',
      required: true,
      label: t('user', 0),
      align: 'left',
      field: (row) =>
        (row.ou ? row.ou.name + (row.user ? ' - ' : '') : '') +
        (row.user ? formatUser(row.user) : ''),
    },
    {
      name: 'totalBalance',
      required: true,
      label: t('totalBalance', 0),
      align: 'left',
      field: 'totalBalance',
      field: (row) => englishToGreekNumber(row.totalBalance),
      serverSideSorting: false,
    },
  ];
} else if (props.type == 'users-per-depunit') {
  columns = [
    {
      name: 'code',
      required: true,
      label: t('codeItem', 0),
      align: 'left',
      field: (row) =>
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
      name: 'name',
      required: true,
      label: t('parDepUnit', 0),
      align: 'left',
      field: (row) =>
        '(' + formatDep(row.depUnit.code) + ') ' + (row.depUnit.name ?? ''),
    },
    {
      name: 'assigned',
      required: true,
      label: t('user', 0),
      align: 'left',
      field: (row) =>
        (row.ou ? row.ou.name + (row.user ? ' - ' : '') : '') +
        (row.user
          ? formatUser(row.user) +
            (row.manager ? ' (' + t('topikos-ypeythynos') + ')' : '')
          : ''),
    },
    {
      name: 'totalBalance',
      required: true,
      label: t('totalBalance', 0),
      align: 'left',
      field: (row) => englishToGreekNumber(row.totalBalance),
      serverSideSorting: false,
    },
    {
      name: 'availableBalance',
      required: true,
      label: t('available', 0),
      align: 'left',
      field: (row) => englishToGreekNumber(row.availableBalance),
      serverSideSorting: false,
    },
    {
      name: 'reservedBalance',
      required: true,
      label: t('reserved', 0),
      align: 'left',
      field: (row: OuAccountBalance) =>
        englishToGreekNumber(
          row.transactionsBalance + row.temporalReservedBalance
        ),
      serverSideSorting: false,
    },
  ];
} else if (props.type == 'users-per-ou') {
  columns = [
    {
      name: 'code',
      required: true,
      label: t('codeItem', 0),
      align: 'left',
      field: (row) =>
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
      name: 'name',
      required: true,
      label: t('department', 0),
      align: 'left',
      field: (row) =>
        '(' + formatDep(row.depUnit.code) + ') ' + (row.depUnit.name ?? ''),
    },
    {
      name: 'assigned',
      required: true,
      label: t('user', 0),
      align: 'left',
      field: (row: AccountBalance) =>
        (row.ou ? row.ou.name + (row.user ? ' - ' : '') : '') +
        (row.user
          ? formatUser(row.user) +
            (row.manager ? ' (' + t('topikos-ypeythynos') + ')' : '')
          : ''),
    },
    {
      name: 'totalBalance',
      required: true,
      label: t('totalBalance', 0),
      align: 'left',
      field: (row) => englishToGreekNumber(row.totalBalance),
      serverSideSorting: false,
    },
    {
      name: 'availableBalance',
      required: true,
      label: t('available', 0),
      align: 'left',
      field: (row) => englishToGreekNumber(row.availableBalance),
      serverSideSorting: false,
    },
    {
      name: 'reservedBalance',
      required: true,
      label: t('reserved', 0),
      align: 'left',
      field: (row: OuAccountBalance) =>
        englishToGreekNumber(
          row.transactionsBalance + row.temporalReservedBalance
        ),
      serverSideSorting: false,
    },
  ];
} else {
  columns = [
    {
      name: 'department',
      label: t('department', 0),
      align: 'left',
      field: (row) => '(' + formatDep(row[4]) + ') ' + row[5],
      serverSideSorting: false,
    },
    {
      name: 'totalBalance',
      required: true,
      label: t('totalBalance', 0),
      align: 'left',
      field: (row) => englishToGreekNumber(row[2]),
      serverSideSorting: false,
    },
    {
      name: 'balance',
      required: true,
      label: t('balance', 0),
      align: 'left',
      field: (row) => englishToGreekNumber(row[1]),
      serverSideSorting: false,
    },
    { name: 'actions' },
  ];
}

const props = defineProps<{
  itemCode: ItemCode;
  type: string;
  depUnitId: Id | null;
  assigneeUserId: Id | null;
  assigneeOuId: Id | null;
  assignedToTopikosYpeythynos?: boolean | null;
}>();

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
    'getAccountBalances',
    queryPagination,
    props.itemCode?.id,
    props.type,
    props.depUnitId,
    props.assigneeUserId,
    props.assigneeOuId,
    props.assignedToTopikosYpeythynos,
  ],
  queryFn: () =>
    getAccountBalances(
      queryPagination.value.size,
      queryPagination.value.index,
      props.itemCode?.id,
      props.type,
      props.depUnitId,
      props.assigneeUserId,
      props.assigneeOuId,
      props.assignedToTopikosYpeythynos
    ),
  onSuccess: (data: PageResponse<AccountBalance>) => {
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

let viewDialogItems = ref(false);
let viewDialogDepartments = ref(false);
let depUnit = ref({ id: null, name: null });
let assigneeUser = ref(null);
let assignedToDepUnit = ref(null);
let assignedToUser = ref(null);
let assigneeOu = ref(null);
let assignedToOu = ref(null);
let temporalReserved = ref(null);
let inTransaction = ref(null);
let inTransactionOrTemporalReserved = ref(null);

function viewBalanceHandler(item) {
  resetVariables();
  assigneeUser.value = item.user ? item.user : null;
  assignedToUser.value = item.user ? true : false;
  assignedToOu.value = item.ou == null ? false : true;
  assigneeOu.value =
    item.ou == null ? null : { id: item.ou.id, name: item.ou.name };
  depUnit.value = item.depUnit ?? item.parDepUnit;
  viewDialogItems.value = true;
}

function viewAssignedBalanceHandler(item) {
  resetVariables();
  assigneeUser.value = item.user ? item.user : null;
  assignedToUser.value = true;
  depUnit.value = item.depUnit ?? item.parDepUnit;
  if (item.itemCode.discreet) {
    viewDialogItems.value = true;
  } else {
    viewDialogDepartments.value = true;
  }
}

function viewBalanceAvailableHandler(item: AccountBalance) {
  resetVariables();
  assigneeUser.value = item.user ? item.user : null;
  assignedToUser.value = item.user ? true : false;
  assignedToOu.value = item.ou == null ? false : true;
  assigneeOu.value =
    item.ou == null ? null : { id: item.ou.id, name: item.ou.name };
  depUnit.value = item.depUnit ?? item.parDepUnit;
  temporalReserved.value = false;
  inTransaction.value = false;
  inTransactionOrTemporalReserved.value = null;
  viewDialogItems.value = true;
}

function viewBalanceReservedHandler(item: AccountBalance) {
  resetVariables();
  assigneeUser.value = item.user ? item.user : null;
  assignedToUser.value = item.user ? true : false;
  assignedToOu.value = item.ou == null ? false : true;
  assigneeOu.value =
    item.ou == null ? null : { id: item.ou.id, name: item.ou.name };
  depUnit.value = item.depUnit ?? item.parDepUnit;
  temporalReserved.value = null;
  inTransaction.value = null;
  inTransactionOrTemporalReserved.value = true;
  viewDialogItems.value = true;
}

function resetVariables() {
  depUnit.value = null;
  assigneeUser.value = null;
  assignedToDepUnit.value = null;
  assignedToUser.value = null;
  assigneeOu.value = null;
  assignedToOu.value = null;
  temporalReserved.value = null;
  inTransaction.value = null;
  inTransactionOrTemporalReserved.value = null;
}

defineEmits([
  'viewBalance',
  'viewTotalBalance',
  'viewUserBalance',
  'viewAssignedBalance',
]);
</script>
