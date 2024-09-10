<template>
  <q-btn
    v-if="metaptosi"
    class="q-mb-md"
    outline
    no-caps
    icon="search"
    @click="metaptosiDialog = true"
  >
    {{ t('addFrommetaptosi') }}
  </q-btn>
  <q-dialog v-model="metaptosiDialog" persistent>
    <q-card style="min-width: 80vw; max-width: 80vw">
      <q-card-section>
        <TableMetaptosiGun
          v-model="metaptosiSelected"
          :multiple-items="multipleItems"
        ></TableMetaptosiGun>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn :label="t('cancel')" outline v-close-popup no-caps />
        <q-btn
          v-if="!multipleItems"
          :label="t('select')"
          color="primary"
          @click="onMetaptosi"
          no-caps
          unelevated
          :disable="metaptosiSelected.length != 1"
        />
        <q-btn
          v-else
          :label="t('metaptosi-multiple')"
          color="primary"
          @click="onMetaptosiMultiple"
          no-caps
          unelevated
          :disable="metaptosiSelected.length < 1"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <div class="row q-col-gutter-sm q-mb-md" v-if="!multipleItems">
    <div class="col-12 col-md-12">
      <q-input
        filled
        v-model="copiedItem.serialNumber"
        :label="t('serialNumber')"
        clearable
        :readonly="!editable"
        data-cy="item-serialNumber"
        :class="restrictions ? 'required' : ''"
        :rules="restrictions ? requiredRule() : null"
        hide-bottom-space
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
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.katastash"
      data-cy="item-katastash"
      :label="t('katastash')"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/katastash"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
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
    <SelectEnum
      filled
      v-model="copiedItem.diametrimaOplou"
      :label="t('diametrimaOplou')"
      data-cy="item-diametrimaOplou"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/diametrimaOplou"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.eidosOplou"
      :label="t('eidosOplou')"
      data-cy="item-eidosOplou"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/eidosOplou"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.markaOplou"
      :label="t('markaOplou')"
      data-cy="item-markaOplou"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/markaOplou"
      @update:modelValue="copiedItem.modeloOplou = null"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md" v-if="copiedItem.markaOplou">
    <SelectEnum
      :key="'modelooplou_' + copiedItem.markaOplou.id"
      filled
      v-model="copiedItem.modeloOplou"
      :label="t('modeloOplou')"
      data-cy="item-modeloOplou"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/modeloOplou"
      :query-params="'markaId=' + copiedItem.markaOplou.id"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.choraKataskevis"
      :label="t('choraKataskevis')"
      data-cy="item-choraKataskevis"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/chora"
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
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import { Gun } from 'src/services/models';
import {computed, ref} from 'vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import InputDate from 'components/inputs/InputDate.vue';
import TableMetaptosiGun from "components/tables/items/TableMetaptosiGun.vue";
import axios from "axios";
import {useValidationRules} from "src/utils/validation-rules";
import {Id} from "src/services/types";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Gun | null;
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

const emit = defineEmits(['update:modelValue', 'onMetaptosiMultiple']);
const copiedItem: Gun = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

let metaptosiDialog = ref(false);
let metaptosiSelected = ref([]);

function onMetaptosi() {
  metaptosiDialog.value = false;
  console.log(JSON.stringify(metaptosiSelected.value));
  let selected = metaptosiSelected.value[0];
  axios
    .get('/api/v1/items-metaptosi/gun/dto/' + selected.id)
    .then((res) => {
      // Clear initial values from the item object
      Object.keys(copiedItem.value).forEach((key) => {
        console.log(key)
        if (['serialNumber', 'paratiriseis', 'katastash', 'idMetaptosis', 'arithmosKykloforiasOchimatos', 'idPompodekti'].includes(key)) {
          copiedItem.value[key] = undefined; // or null, or any other appropriate value
        }
      });
      Object.assign(copiedItem.value, res.data);
      console.log(res.data)
      //copiedItem.value.paratiriseis = res.data.paratiriseis;
    });
}

function onMetaptosiMultiple() {
  metaptosiDialog.value = false;
  let selected = metaptosiSelected.value.map(mt => mt.id);
  emit('onMetaptosiMultiple', selected)
}

const { requiredRule } = useValidationRules();
</script>
