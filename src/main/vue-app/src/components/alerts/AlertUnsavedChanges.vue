<template>
  <q-banner
    rounded
    class="bg-warning text-white"
    dense
    v-if="hasUnsavedChanges"
  >
    {{ t('unsavedChanges') }}
  </q-banner>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { ref, watch } from 'vue';

const { t } = useI18n();

const props = defineProps<{
  objectToWatch: object;
}>();

const initialObj = JSON.stringify(props.objectToWatch);

const hasUnsavedChanges = ref(false);

watch(
  () => props.objectToWatch,
  () => {
    if (JSON.stringify(props.objectToWatch) !== initialObj) {
      hasUnsavedChanges.value = true;
    } else {
      hasUnsavedChanges.value = false;
    }
  },
  { deep: true }
);
</script>

<style lang="sass"></style>
