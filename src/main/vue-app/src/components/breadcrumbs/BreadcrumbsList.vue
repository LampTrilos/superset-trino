<template>
  <div style="min-height: 40px">
    <q-breadcrumbs v-if="breadcrumbs && breadcrumbs.length != 0" gutter="sm">
      <q-breadcrumbs-el
        v-for="breadcrumb in breadcrumbsI18n"
        :key="breadcrumb.label"
        :label="breadcrumb.label"
        :to="breadcrumb.to"
      />
    </q-breadcrumbs>
    <!-- This replicates the spacing when no breadcrumbs-->
    <div v-else class="q-gutter-sm"></div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';

const { t } = useI18n();

export interface BreadcrumbProps {
  label: string | object;
  to?: string;
}

const props = defineProps({
  breadcrumbs: {
    type: Array as () => BreadcrumbProps[],
    default: () => [],
  },
});

const breadcrumbsI18n = computed(() => {
  // Map over the props.breadcrumbs array and generate a new array of breadcrumbs with the label property translated
  return props.breadcrumbs.map((breadcrumb) => {
    return { ...breadcrumb, label: translateBreadcrumbLabel(breadcrumb.label) };
  });
});

// Define the function that translates a breadcrumb label
function translateBreadcrumbLabel(label) {
  // Initialize the translated label variable
  let labelI18n = '';
  // Check the type of the label argument
  if (typeof label === 'string' || typeof label === 'number') {
    // If the label is a string, use the t function to translate it
    labelI18n = t(label);
  } else if (Array.isArray(label)) {
    // If the label is an array, recursively call i18nLabel on each element of the array and join the results together with a space
    labelI18n = label.map((lb) => translateBreadcrumbLabel(lb)).join(' ');
  } else if (typeof label === 'object') {
    // If the label is an object, use the t function to translate the key property with the plural property as a context parameter
    labelI18n = t(label.key, label.content);
  }
  // Return the translated label
  return labelI18n;
}
</script>
