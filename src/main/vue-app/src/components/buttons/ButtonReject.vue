<template>
  <q-btn
    v-if="data"
    color="negative"
    outline
    no-caps
    class="q-ml-sm"
    data-cy="dialog-reject"
    @click="rejectHandler"
  >
    {{ t('reject') }}
  </q-btn>
  <q-dialog v-model="rejectDialog" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">
          {{ t('doYouConfirmThisAction') }}. {{ t('transaction', 0) }}:
          {{ selectedItem.udc }}
        </div>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn :label="t('cancel')" outline v-close-popup no-caps />
        <q-btn
          :label="t('reject') + ' ' + t('transaction', 2)"
          color="primary"
          @click="onSubmitReject"
          no-caps
          unelevated
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { CompoundTransaction } from 'src/services/models';
import { reactive, ref } from 'vue';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import {
  hasPermissionsToRejectCompoundTransaction,
  rejectCompoundTransaction,
} from 'src/services/transactions';
import { Loading, Notify } from 'quasar';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    compTransaction: CompoundTransaction;
    pushRouter?: string;
  }>(),
  {
    pushRouter: 'CompoundTransactionsForSign',
  }
);

const router = useRouter();

const { data } = useQuery({
  queryKey: [
    'hasPermissionsToRejectCompoundTransaction',
    props.compTransaction.id,
  ],
  queryFn: () =>
    hasPermissionsToRejectCompoundTransaction(props.compTransaction.id),
});

let rejectDialog = ref(false);
const queryClient = useQueryClient();
const { mutate: rejectCompoundTransactionM } = useMutation(
  rejectCompoundTransaction,
  {
    onMutate: () => {
      Loading.show();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['getCompoundTransactions'],
      });
      queryClient.invalidateQueries({
        queryKey: ['getCompoundTransactionsForSign'],
      });
      queryClient.invalidateQueries({
        queryKey: ['getCompoundTransactionsPending'],
      });
      queryClient.invalidateQueries({
        queryKey: ['getMyCompoundTransactionsForSign'],
      });
      queryClient.invalidateQueries({
        queryKey: ['getMyCompoundTransactionsCompleted'],
      });
      queryClient.invalidateQueries({
        queryKey: ['getMyCompoundTransactionsPending'],
      });
      Notify.create({
        message: t('reject-successfully'),
      });
      rejectDialog.value = false;
      router.push({ name: props.pushRouter });
    },
    onSettled: () => {
      Loading.hide();
    },
  }
);

let selectedItem = reactive({ id: null, udc: null });
function rejectHandler() {
  rejectDialog.value = true;
  selectedItem.id = props.compTransaction.id;
  selectedItem.udc = props.compTransaction.udc;
}

function onSubmitReject() {
  rejectCompoundTransactionM(selectedItem.id);
}
</script>

<style lang="sass"></style>
