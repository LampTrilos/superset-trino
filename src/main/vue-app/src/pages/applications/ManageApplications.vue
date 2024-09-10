<template>
  <TableManagedApplications :title="t('Allowed_Applications')">

  </TableManagedApplications>
</template>
<script setup lang="ts">
import TableManagedApplications from "components/tables/TableManagedApplications.vue";
import { useRouter } from 'vue-router';
const router = useRouter();
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
</script>
