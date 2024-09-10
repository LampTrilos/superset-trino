<template>
  <q-btn
    v-if="data"
    color="primary"
    no-caps
    unelevated
    class="q-ml-sm q-mb-sm"
    data-cy="button-sign"
    @click="signHandler"
  >
    {{ t('sign') }}
  </q-btn>
  <q-dialog v-model="signDialog" persistent>
    <q-card style="min-width: 80vw; max-width: 80vw">
      <q-card-section>
        <div class="text-h6">
          {{ t('enter-verify-code') }}. {{ t('transaction', 0) }}:
          {{ selectedItem.udc }}
        </div>
      </q-card-section>
      <q-card-section>
        <TransactionFilesForSign :comp-transaction-id="compTransaction.id" />
      </q-card-section>
      <q-card-section>
        <q-form @submit="onSubmitSign">
          <div class="row q-col-gutter-sm">
            <div class="col-12">
              <q-input
                filled
                v-model="verifyCode"
                :label="t('verifyCode')"
                class="required"
                :rules="requiredRule()"
                clearable
                data-cy="verifyCode"
              />
            </div>
            <div class="col-12 text-primary cursor-pointer text-underline-hover" @click="resendEmailHandler">
              {{ t('resend-email') }}
            </div>
          </div>
          <div class="q-mt-lg text-right">
            <q-btn :label="t('close')" outline v-close-popup no-caps />
            <q-btn
              class="q-ml-sm"
              :label="t('sign')"
              color="primary"
              type="submit"
              unelevated
              no-caps
              data-cy="dialog-sign"
            />
          </div>
        </q-form>
      </q-card-section>
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
  hasPermissionsToSignCompoundTransaction, resendNotificationAndEmailCompoundTransaction,
  signCompoundTransaction,
} from 'src/services/transactions';
import { Loading, Notify } from 'quasar';
import { useValidationRules } from 'src/utils/validation-rules';
import TransactionFilesForSign from 'components/transactions/TransactionFilesForSign.vue';

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
    'hasPermissionsToSignCompoundTransaction',
    props.compTransaction.id,
  ],
  queryFn: () =>
    hasPermissionsToSignCompoundTransaction(props.compTransaction.id),
});

let verifyCode = ref(null);
let signDialog = ref(false);
let selectedItem = reactive({ id: null, udc: null });
const { requiredRule } = useValidationRules();
function signHandler() {
  signDialog.value = true;
  selectedItem.id = props.compTransaction.id;
  selectedItem.udc = props.compTransaction.udc;
}

function onSubmitSign() {
  console.log(verifyCode.value);
  signCompoundTransactionM({
    id: props.compTransaction.id,
    verifyCode: verifyCode.value,
  });
}

const queryClient = useQueryClient();
const { mutate: signCompoundTransactionM } = useMutation(
  signCompoundTransaction,
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
        message: t('signed-successfully'),
      });
      signDialog.value = false;
      router.push({ name: props.pushRouter });
    },
    onSettled: () => {
      Loading.hide();
    },
  }
);

function resendEmailHandler() {
  resendEmailM(props.compTransaction.id);
}

const { mutate: resendEmailM } = useMutation(
  resendNotificationAndEmailCompoundTransaction,
  {
    onMutate: () => {
      Loading.show();
    },
    onSuccess: () => {
      Notify.create({
        message: t('resend-email-successfully'),
      });
    },
    onSettled: () => {
      Loading.hide();
    },
  }
);
</script>
