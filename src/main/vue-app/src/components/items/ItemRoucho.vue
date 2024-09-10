<template>
  <div class="col-12 col-md-12">
    {{ t('serialNumber') + ': ' + copiedItem.serialNumber}}
  </div>
  <div class="col-12 col-md-12 text-grey q-mb-md">
    Ο σειριακός παράγεται αυτόματα από το σύστημα
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.megethos"
      :label="t('megethos')"
      data-cy="item-rouxismosMegethos"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/rouxismosMegethos"
      :query-params="'eidos=ROUCHO'"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <q-input
      v-model="copiedItem.paratiriseis"
      :placeholder="t('paratiriseis')"
      data-cy="item-paratiriseis"
      filled
      clearable
      type="textarea"
      autogrow
      maxlength="1000"
      :readonly="!editable"
      hide-hint
      hint="Μέχρι 1000 χαρακτήρες"
    ></q-input>
  </div>
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import {computed, watch} from 'vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import { Rouxismos} from 'src/services/models';
import {useValidationRules} from "src/utils/validation-rules";
import {Id} from "src/services/types";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Rouxismos | null;
    editable: boolean;
    multipleItems?: boolean;
    metaptosi?: boolean;
    restrictions?: boolean;
    kae?: string;
    itemCodeId?: Id;
    depUnitCode?: string;
  }>(),
  {
    modelValue: () => null,
    editable: true,
    multipleItems: false,
  }
);

const emit = defineEmits(['update:modelValue']);
const copiedItem: Rouxismos = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});
const { requiredRule } = useValidationRules();

watch(
  () => copiedItem,
  () => {
    copiedItem.value.serialNumber = 'SZ' + (copiedItem.value.megethos ? copiedItem.value.megethos.description : '');
  },
  { deep: true, immediate: true }
);
</script>
