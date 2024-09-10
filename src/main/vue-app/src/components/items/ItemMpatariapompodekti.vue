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
        <TableMetaptosiMpatariaPompodekti
          v-model="metaptosiSelected"
          :multiple-items="multipleItems"
        ></TableMetaptosiMpatariaPompodekti>
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
      v-model="copiedItem.eidosPompodekti"
      :label="t('eidosPompodekti')"
      data-cy="item-eidosPompodekti"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/eidosPompodekti"
      :query-params="'id=1,2,3'"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md" v-if="copiedItem.eidosPompodekti">
    <SelectEnum
      :key="'eidosPompodekti' + copiedItem.eidosPompodekti.id"
      filled
      v-model="copiedItem.markaPompodekti"
      :label="t('marka')"
      data-cy="item-markaPompodekti"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/markaPompodekti"
      :query-params="'idEidosPompodekti=' + (copiedItem.eidosPompodekti.id == '1' ? '1,3' : (copiedItem.eidosPompodekti.id == '2' ? '2,3' : '1,2,3'))"
      @update:modelValue="copiedItem.modeloPompodekti = null"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md" v-if="copiedItem.markaPompodekti">
    <SelectEnum
      :key="'modeloPompodekti_' + copiedItem.markaPompodekti.id"
      filled
      v-model="copiedItem.modeloPompodekti"
      :label="t('modelo')"
      data-cy="item-modeloPompodekti"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/modeloPompodekti"
      :query-params="'idMarkas=' + copiedItem.markaPompodekti.id"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
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
import {computed, ref} from 'vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import { Parelkomenopompodekti } from 'src/services/models';
import InputDate from 'components/inputs/InputDate.vue';
import TableMetaptosiMpatariaPompodekti from "components/tables/items/TableMetaptosiMpatariaPompodekti.vue";
import axios from "axios";
import {useValidationRules} from "src/utils/validation-rules";
import {Id} from "src/services/types";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Parelkomenopompodekti | null;
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
const copiedItem: Parelkomenopompodekti = computed({
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
    .get('/api/v1/items-metaptosi/mpatariaPompodekti/dto/' + selected.id)
    .then((res) => {
      // Clear initial values from the item object
      Object.keys(copiedItem.value).forEach((key) => {
        console.log(key)
        if (['serialNumber', 'paratiriseis', 'katastash', 'idMetaptosis', 'eidosPompodekti', 'markaPompodekti', 'modeloPompodekti', 'eidosParelkomenou'].includes(key)) {
          copiedItem.value[key] = undefined; // or null, or any other appropriate value
        }
      });
      Object.assign(copiedItem.value, res.data);
      console.log(res.data)
    });
}

function onMetaptosiMultiple() {
  metaptosiDialog.value = false;
  let selected = metaptosiSelected.value.map(mt => mt.id);
  emit('onMetaptosiMultiple', selected)
}

const { requiredRule } = useValidationRules();
</script>
