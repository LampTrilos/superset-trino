<template>
  <div v-if="isFetching">
    <q-spinner-dots color="primary" size="2em" />
    <q-tooltip :offset="[0, 8]">QSpinnerDots</q-tooltip>
  </div>
  <q-markup-table v-else flat class="q-px-xl">
    <thead>
      <tr>
        <th class="text-left" style="width: 600px">{{ t('codeItem', 0) }}</th>
        <th class="text-left">{{ t('serialNumber') }}</th>
        <th class="text-left">{{ t('amount') }}</th>
        <th class="text-left"></th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="iTransaction in data?.content"
        :key="'i-transaction-' + iTransaction.id"
      >
        <td style="max-width: 600px" class="ellipsis">
          {{
            '(' +
            formatItemCodeFull(iTransaction.itemCode) +
            ') ' +
            (iTransaction.itemCodeEntity?.name ?? '')
          }}
        </td>
        <td>
          <router-link
            v-if="iTransaction.itemJson"
            class="text-primary"
            :to="{
              name: 'ItemView',
              params: { id: iTransaction.itemJson?.id },
            }"
            >{{ iTransaction.itemJson?.serialNumber }}</router-link
          >
        </td>
        <td>{{ englishToGreekNumber(iTransaction.amount) }}</td>
        <td>
          <q-btn
            v-if="iTransaction.itemJson"
            round
            flat
            size="sm"
            icon="sym_o_edit"
            color="primary"
            :to="{
              name: 'ItemView',
              params: { id: iTransaction.itemJson?.id },
            }"
            ><q-tooltip>{{ t('edit') }}</q-tooltip></q-btn
          >
        </td>
      </tr>
    </tbody>
  </q-markup-table>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { CompoundTransaction } from 'src/services/models';
import { useQuery } from '@tanstack/vue-query';
import { getITransactionsByQuery } from 'src/services/transactions';
import { useNumberFormatter } from 'src/utils/number-formatter';
const { englishToGreekNumber } = useNumberFormatter();

const { t } = useI18n();
const { formatItemCodeFull } = useCodeFormatter();

const props = defineProps<{
  compoundTransaction: CompoundTransaction;
}>();

let query = 'compTransaction=' + props.compoundTransaction.id + '&size=-1';
const { data, isFetching } = useQuery({
  queryKey: ['itemCodeCategories', query],
  queryFn: () => getITransactionsByQuery(query),
});
</script>
