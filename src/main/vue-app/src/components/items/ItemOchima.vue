<template>
  <div>Τα οχήματα πρέπει να εισαχθούν το καθένα ξεχωριστά (Υποχρεωτικά ποσότητα 1)</div>
  <div class="row q-col-gutter-sm q-mt-md q-mb-md">
    <div class="col-8 col-md-8">
      <q-input
        filled
        v-model="copiedItem.serialNumber"
        :label="'Πινακίδα'"
        clearable
        :readonly="!editable"
        data-cy="item-serialNumber"
        :class="'required'"
        :rules="requiredRule()"
        hide-bottom-space
      >
      </q-input>
    </div>
    <div v-if="copiedItem.serialNumber" class="col-4">
      <q-input v-model="validOchima" readonly borderless :rules="requiredRuleOchima()">
        <template v-slot:prepend>
          <q-icon v-if="validOchima == 'Επαληθεύτηκε'" name="check" color="green" />
          <q-icon v-else name="warning" color="yellow" />
        </template>
      </q-input>
    </div>
  </div>
<!--  <div class="row q-col-gutter-sm q-mb-md" v-else>
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
  </div>-->
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.katastash"
      data-cy="item-katastash"
      :label="t('katastash')"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/katastash"
    />
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
  <div class="row q-col-gutter-sm q-mt-md" v-if="copiedItem.idMetaptosis">
    <div class="col-12 col-md-6">
      <q-input
        filled
        autogrow
        v-model="copiedItem.idMetaptosis"
        :label="t('idMetaptosis')"
        data-cy="item-idMetaptosis"
        clearable
        :readonly="true"
      />
    </div>
  </div>
  <div class="col-12 col-md-12 q-mt-md">
    Τα παρακάτω στοιχεία προέρχονται από το πληροφοριακό σύστημα οχήματα
  </div>
  <div v-if="copiedItem.serialNumber" :key="copiedItem.serialNumber">
    <ItemOchimaSoap v-model="validOchima" :arithmos-kykloforias-ochimatos="copiedItem.serialNumber" :show-details="true"></ItemOchimaSoap>
  </div>
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import { Ochima } from 'src/services/models';
import {computed, ref} from 'vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import InputDate from 'components/inputs/InputDate.vue';
import {getOchimaByRegNumSoap} from "src/services/items";
import ItemOchimaSoap from "components/items/ItemOchimaSoap.vue";
import {useValidationRules} from "src/utils/validation-rules";
import {Id} from "src/services/types";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Ochima | null;
    editable: boolean;
    metaptosi?: boolean;
    restrictions?: boolean;
    multipleItems?: boolean;
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
const copiedItem: Ochima = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

let ochimaSoap = ref();
if (props.modelValue && props.modelValue.serialNumber) {
  ochimaSoap.value = getOchimaByRegNumSoap(props.modelValue.serialNumber)
}

let validOchima = ref('Δεν επαληθεύτηκε');
const { requiredRule, requiredRuleOchima } = useValidationRules();
</script>
