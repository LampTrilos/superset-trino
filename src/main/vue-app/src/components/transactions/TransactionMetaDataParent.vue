<template>
  <div class="column q-col-gutter-sm">
    <div>{{ t('parentTransaction') }}</div>
    <div class="row">
      <div>
        <router-link
          class="text-primary"
          :to="{
            name: linkName,
            params: { id: transaction.parentTransaction.id },
            query: { type: transaction.parentTransaction.type?.id },
          }"
          >{{ transaction.parentTransaction.udc }}</router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { CompoundTransaction } from 'src/services/models';
import { computed } from 'vue';

const { t } = useI18n();

const props = defineProps<{
  transaction: CompoundTransaction;
}>();

const linkName = computed(() => {
  const status = props.transaction.parentTransaction.status;
  switch (status) {
    case 'COMPLETED':
      return 'CompoundTransaction';
    case 'REJECTED':
      return 'CompoundTransactionRejected';
    case 'FAILED':
      return 'CompoundTransactionRejected';
    case 'FOR_SIGNATURE':
      return 'CompoundTransactionPending';
    case 'DRAFT':
      return 'CompoundTransactionEdit';
    default:
      return 'CompoundTransaction';
  }
});
</script>

<style lang="sass"></style>
