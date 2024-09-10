<template>
  <div class="row q-col-gutter-sm">
    <div><q-icon name="person" /></div>
    <div>{{ t('author') }} :</div>
    <div class="text-bold" v-if="transaction.author != null">
      {{ formatUser(transaction.author) }}
    </div>
  </div>
  <div class="row q-col-gutter-sm">
    <div><q-icon name="calendar_month" /></div>
    <div>{{ t('crTmp') }} :</div>
    <div class="text-bold">
      {{ formatDateTimeLocale(transaction.createdTimestamp) }}
    </div>
  </div>
  <div class="row q-col-gutter-sm">
    <div><q-icon name="calendar_month" /></div>
    <div>{{ t('upTmp') }} :</div>
    <div class="text-bold">
      {{ formatDateTimeLocale(transaction.updatedTimestamp) }}
    </div>
  </div>
  <div class="row q-col-gutter-sm">
    <div><q-icon name="flag" /></div>
    <div>{{ t('status') }} :</div>
    <div class="text-bold" v-if="transaction.status != null">
      {{ t(transaction.status) }}
      {{
        transaction.pendingDescription
          ? ' - ' + transaction.pendingDescription
          : ''
      }}
    </div>
  </div>
  <div class="row q-col-gutter-sm" v-if="transaction.status == 'REJECTED'">
    <div><q-icon name="person" /></div>
    <div>{{ t('rejectedFromUser') }} :</div>
    <div class="text-bold" v-if="transaction.rejectedFromUser != null">
      {{ formatUser(transaction.rejectedFromUser) }}
    </div>
  </div>
  <div class="row q-col-gutter-sm">
    <div><q-icon name="mdi-signature-freehand" /></div>
    <div>{{ t('signature', 1) }} :</div>
    <div class="text-bold">
      {{ transaction.signaturesCount }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useDateFormatter } from 'src/utils/date-formatter';
import { CompoundTransaction } from 'src/services/models';
import { useCodeFormatter } from 'src/utils/code-formatter';
const { formatDateTimeLocale } = useDateFormatter();
const { formatUser } = useCodeFormatter();

const { t } = useI18n();

defineProps<{
  transaction: CompoundTransaction;
}>();
</script>

<style lang="sass"></style>
