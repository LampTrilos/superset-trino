<template>
  <q-layout view="hHh lpR fFf">
    <q-header class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-icon size="32px" name="img:/icons/logo-white.svg" />
          Σύστημα Διαχείρισης Υλικών
        </q-toolbar-title>
        <q-space />
        <q-btn
          v-if="authenticated"
          square
          flat
          no-caps
          :to="{ name: 'Dashboard' }"
        >
          {{ t('dashboard') }}
        </q-btn>
        <q-btn v-else square flat no-caps @click="login">
          {{ t('login') }}
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view :key="$route.fullPath" />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { keycloak } from 'boot/authentication';
import { useAccountStore } from '../stores/account';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const { t } = useI18n();

const { authenticated } = storeToRefs(useAccountStore());

const router = useRouter();
function login() {
  router.push({
    name: 'AutoLogin',
  });
}
</script>
