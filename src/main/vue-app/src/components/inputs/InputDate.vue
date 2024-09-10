<template>
  <q-input v-model="value" :mask="mask" :readonly="readonly">
    <template #append>
      <q-icon name="event" class="cursor-pointer">
        <q-popup-proxy
          cover
          transition-show="scale"
          transition-hide="scale"
          v-if="!readonly"
        >
          <q-date v-model="value" :mask="dateFormat">
            <div class="row items-center justify-end">
              <q-btn v-close-popup :label="t('close')" outline no-caps />
            </div>
          </q-date>
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
import { useDateFormatter } from 'src/utils/date-formatter';

const { t } = useI18n();
const { getDateFormat, parseDate, formatDateToServerSideFormat } =
  useDateFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: string | null;
    readonly?: boolean | undefined;
  }>(),
  {
    modelValue: () => null,
    readonly: () => false,
  }
);

const emit = defineEmits(['update:modelValue']);

const dateFormat = getDateFormat(); // Get the date format from the useDateFormatter utility function
const mask = dateFormat.replace(/y|m|d/gi, '#'); // Replace year, month, and day with "#" to create a mask for the date input field
//console.debug(mask);

const value = computed({
  get() {
    return parseDate(props.modelValue);
  },
  set(value) {
    emit('update:modelValue', formatDateToServerSideFormat(value)); // Emit an event with the updated date value in the server-side format
  },
});
</script>
