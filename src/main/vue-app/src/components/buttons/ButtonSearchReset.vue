<template>
  <q-btn icon="search_off" unelevated no-caps @click="triggerSearchReset">
    <q-tooltip>{{ t('searchReset') }}</q-tooltip>
  </q-btn>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

const { t } = useI18n();

const router = useRouter();
const route = useRoute();
function triggerSearchReset() {
  // Define the new query parameters
  const updatedQueryParams = {
    // Set the "index" property to 0, which represents the first page
    index: 0,
    // Merge query sort param
    sort: route.query.sort || undefined,
  };

  // Update the URL with the new query parameters using the "router.push()" method
  try {
    router.push({ query: updatedQueryParams });
  } catch (error) {
    console.error('Failed to update query parameters:', error);
  }
}
</script>

<style lang="sass"></style>
