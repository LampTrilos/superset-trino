<template>
  <q-btn
    color="primary"
    icon="search"
    unelevated
    no-caps
    @click="triggerSearch"
  >
    {{ t('search') }}
  </q-btn>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    queryParams: object | null;
  }>(),
  {
    queryParams: () => null,
  }
);

const router = useRouter();
function triggerSearch() {
  // Define the new query parameters
  const updatedQueryParams = {
    // Merge any additional properties defined by the "search" object
    ...props.queryParams,
    // Set the "index" property to 0, which represents the first page
    index: 0,
  };

  // Filter out null values, empty values and empty arrays from the updated query parameters
  const filteredQueryParams = {};
  for (const key in updatedQueryParams) {
    if (
      updatedQueryParams.hasOwnProperty(key) &&
      updatedQueryParams[key] !== null &&
      updatedQueryParams[key] !== '' &&
      !(
        Array.isArray(updatedQueryParams[key]) &&
        updatedQueryParams[key].length === 0
      )
    ) {
      filteredQueryParams[key] = updatedQueryParams[key];
    }
  }

  // Update the URL with the new query parameters using the "router.push()" method
  try {
    router.push({ query: filteredQueryParams });
  } catch (error) {
    console.error('Failed to update query parameters:', error);
  }
}

defineExpose({
  triggerSearch,
});
</script>

<style lang="sass"></style>
