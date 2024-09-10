<template>
  <!--  <q-markup-table flat class="q-px-xl">
    <thead>
      <tr>
        <th class="text-left">{{ t('assigned', 0) }}</th>
        <th class="text-left">{{ t('balance', 0) }}</th>
        <th v-if="balances.itemCode.discreet"></th>
      </tr>
    </thead>
    <tbody>
      <tr v-if="balances.balanceUserAssigned" key="balance-user-assigned">
        <td>
          {{ t('user-assigned') }}
        </td>
        <td>{{ balances.balanceUserAssigned }}</td>
        <td v-if="balances.itemCode.discreet">
          <q-btn
            flat
            size="md"
            color="primary"
            no-caps
            @click="viewHandlerUserAssigned()"
            >{{ t('view') }}</q-btn
          >
        </td>
      </tr>

      <tr v-if="balances.balanceNotAssigned" key="balance-not-assigned">
        <td>
          {{ t('not-assigned') }}
        </td>
        <td>{{ balances.balanceNotAssigned }}</td>
        <td v-if="balances.itemCode.discreet">
          <q-btn
            flat
            size="md"
            color="primary"
            @click="viewHandlerNotAssigned()"
            no-caps
            >{{ t('view') }}</q-btn
          >
        </td>
      </tr>
    </tbody>
  </q-markup-table>-->
  <TableBalances
    :item-code="itemCode"
    :type="type ?? ''"
    :dep-unit-id="depUnitId"
  ></TableBalances>
  <DialogItems
    v-model="viewDialog"
    :assigned-to-user="assignedToUser"
    :assigned-to-dep-unit="assignedToDepUnit"
    :item-code="itemCode"
    :dep-unit="depUnit"
    :assignee-user="assigneeUser"
  />
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { DepUnit, ItemCode, TotalBalance } from 'src/services/models';
import { ref } from 'vue';
import DialogItems from 'components/dialog/DialogItems.vue';
import { useQuery } from '@tanstack/vue-query';
import { getITransactionsByQuery } from 'src/services/transactions';
import TableBalances from 'components/tables/TableBalances.vue';
import { Id } from 'src/services/types';

const { t } = useI18n();
const { formatDep } = useCodeFormatter();

const props = defineProps<{
  itemCode: ItemCode;
  type: string;
  depUnitId: Id | null;
}>();

let viewDialog = ref(false);

let depUnit = ref(null);
let assigneeUser = ref(null);
let assignedToDepUnit = ref(null);
let assignedToUser = ref(null);

function viewHandler(item) {
  depUnit.value = item.genDepUnit ?? item.parDepUnit ?? null;
  assigneeUser.value = null;
  assignedToDepUnit.value = null;
  assignedToUser.value = null;
  viewDialog.value = true;
}

function viewHandlerUserAssigned() {
  depUnit.value = null;
  assigneeUser.value = null;
  assignedToDepUnit.value = null;
  assignedToUser.value = true;
  viewDialog.value = true;
}

function viewHandlerNotAssigned() {
  depUnit.value = null;
  assigneeUser.value = null;
  assignedToDepUnit.value = false;
  assignedToUser.value = null;
  viewDialog.value = true;
}
</script>
