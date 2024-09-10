<template>
  <q-input v-model="copiedValue" :readonly="readonly"> </q-input>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
import { useNumberFormatter } from 'src/utils/number-formatter';
const { greekToEnglishNumber, englishToGreekNumber } = useNumberFormatter();

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: number | string | null;
    readonly?: boolean | undefined;
  }>(),
  {
    modelValue: () => null,
    readonly: () => false,
  }
);

const emit = defineEmits(['update:modelValue']);
const copiedValue = computed({
  get: () => englishToGreekNumber(props.modelValue),
  set: (value) => emit('update:modelValue', greekToEnglishNumber(value)),
});
</script>
