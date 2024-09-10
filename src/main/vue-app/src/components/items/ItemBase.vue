<template>
  <div class="row q-col-gutter-sm q-mb-md" v-if="!multipleItems">
    <div class="col-12 col-md-12">
      <q-input
        filled
        v-model="copiedItem.serialNumber"
        :label="t('serialNumber')"
        clearable
        :readonly="!editable"
        data-cy="item-serialNumber"
      />
    </div>
  </div>
  <div class="row q-col-gutter-sm q-mb-md" v-else>
    <div class="col-12 col-md-12">
      <q-input
        filled
        v-model="copiedItem.serialNumbers"
        :label="t('serialNumbers')"
        clearable
        :readonly="!editable"
        type="textarea"
        data-cy="item-serialNumbers"
        :hint="copiedItem.serialNumbers ? 'Πλήθος σειριακών: ' + copiedItem.serialNumbers.split(',').length : undefined"
      />
    </div>
  </div>
  <div
    class="col-12 col-md-12 q-mb-md"
    v-if="copiedItem.katastash && copiedItem.katastash.id == '7'"
  >
    <InputDate
      :label="t('imerominiaSumvantos')"
      v-model="copiedItem.imerominiaSumvantos"
      data-cy="item-imerominiaSumvantos"
      filled
      clearable
      :readonly="!editable"
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
import { Item } from 'src/services/models';
import { computed } from 'vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import InputDate from 'components/inputs/InputDate.vue';
import {Id} from "src/services/types";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Item | null;
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
const copiedItem: Item = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});
</script>
