<template>
  <div>Έχετε αποσυνδεθεί από το σύστημα...</div>
  <q-btn color="primary" outline no-caps @click="login">
    {{ t('login') }}
  </q-btn>
</template>
<script setup lang="ts">
import { useAccountStore } from '../../stores/account';
import { useQueryClient } from '@tanstack/vue-query';
import { useI18n } from 'vue-i18n';

const accountStore = useAccountStore();
import { useRouter } from 'vue-router';
const router = useRouter();
const { t } = useI18n();

const queryClient = useQueryClient();
queryClient.clear();
localStorage.clear();
sessionStorage.clear();
accountStore.$reset();

if (process.env.NODE_ENV == 'production') {
  window.location.href = "https://tws.ydt/myPage";
}

function login() {
  router.push({
    name: 'AutoLogin',
  });
}
</script>
