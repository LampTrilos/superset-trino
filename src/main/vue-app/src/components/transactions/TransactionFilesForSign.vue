<template>
  <div class="row q-col-gutter-sm q-mb-lg">
    <div class="col-12 col-md-12">Έγγραφο(α) προς υπογραφή</div>
    <div class="col-12 col-md-12">
      <MarkupTableFiles
        :id="compTransactionId"
        :query-key="'getCompoundTransactionFilesForSign'"
        :get-rows-function="getCompoundTransactionFilesForSign"
        :download-row-function="downloadCompoundTransactionFile"
        :preview-row-function="previewCompoundTransactionFile"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  downloadCompoundTransactionFile,
  getCompoundTransactionFilesForSign,
  previewCompoundTransactionFile,
} from 'src/services/transactions';
import MarkupTableFiles from 'components/tables/MarkupTableFiles.vue';
import { Id } from 'src/services/types';

defineProps<{
  compTransactionId: Id;
}>();
</script>
