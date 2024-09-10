<template>
  <div class="column q-col-gutter-sm">
    <div>{{ t('childrenTransactions') }}</div>
    <div
      class="column"
      v-for="childTransaction in childrenTransactions"
      :key="'child-transaction-' + childTransaction.id"
    >
      <div class="text-bold">
        {{
          '(' +
          formatDep(childTransaction[accountEntity]?.code) +
          ') ' +
          (childTransaction[accountEntity]?.name ?? '') +
          ': '
        }}
      </div>
      <div class="q-mb-sm">
        <router-link
          class="text-primary"
          :to="{
            name: linkName,
            params: { id: childTransaction.id },
            query: { type: childTransaction.type?.id },
          }"
          >{{ childTransaction.udc }}</router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { CompoundTransaction } from 'src/services/models';
import { useQuery } from '@tanstack/vue-query';
import { getChildrenCompoundTransaction } from 'src/services/transactions';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { computed } from 'vue';
const { formatDep } = useCodeFormatter();

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    transaction: CompoundTransaction;
    accountEntity?: string;
  }>(),
  {
    accountEntity: () => 'fromAccountEntity',
  }
);

const { data: childrenTransactions } = useQuery({
  queryKey: ['getChildrenCompoundTransaction', props.transaction.id],
  queryFn: () => getChildrenCompoundTransaction(props.transaction.id),
  select: (data) => data.content,
  enabled: props.transaction.id != null,
});

const linkName = computed(() => {
  const status = props.transaction.status;
  switch (status) {
    case 'COMPLETED':
      return 'CompoundTransaction';
    case 'REJECTED':
      return 'CompoundTransactionRejected';
    case 'FAILED':
      return 'CompoundTransactionRejected';
    case 'DRAFT':
      return 'CompoundTransactionEdit';
    case 'FOR_SIGNATURE':
      return 'CompoundTransactionSign';
    default:
      return 'CompoundTransaction';
  }
});
</script>
