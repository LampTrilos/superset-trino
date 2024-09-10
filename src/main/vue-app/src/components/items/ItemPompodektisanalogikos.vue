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
        <TableMetaptosiPompodektis
          v-model="metaptosiSelected"
          :eidos="'pompodektisAnalogikos'"
          :multiple-items="multipleItems"
        ></TableMetaptosiPompodektis>
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
  <div class="col-12 col-md-12 q-mb-md"
       v-if="copiedItem.katastash && copiedItem.katastash.exeiYpokatastasi">
    <SelectEnum
      filled
      v-model="copiedItem.ypokatastasiPompodekti"
      :label="t('ypokatastasiPompodekti')"
      data-cy="item-ypokatastasiPompodekti"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/ypokatastasiPompodekti"
      :class="restrictions ? ((copiedItem.katastash && copiedItem.katastash.exeiYpokatastasi) ? 'required' : '') : ''"
      :rules="restrictions ? ((copiedItem.katastash && copiedItem.katastash.exeiYpokatastasi) ? requiredRule() : null) : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.katigoriaAnalogikouPompodekti"
      :label="t('katigoriaAnalogikouPompodekti')"
      data-cy="item-katigoriaAnalogikouPompodekti"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/katigoriaAnalogikouPompodekti"
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnum
      filled
      v-model="copiedItem.markaPompodekti"
      :label="t('marka')"
      data-cy="item-markaPompodekti"
      clearable
      :readonly="!editable"
      enum-end-point="item-enums/markaPompodekti"
      :query-params="'idEidosPompodekti=2,3'"
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
  <div class=" q-mb-md q-mb-md">
    <div class="row q-col-gutter-sm">
      <div class="col-8">
        <q-input
          filled
          v-model="copiedItem.arithmosKykloforiasOchimatos"
          :label="t('arithmosKykloforiasOchimatos')"
          data-cy="item-arithmosKykloforiasOchimatos"
          clearable
          :readonly="!editable"
          :hint="restrictions ? (copiedItem.modeloPompodekti && copiedItem.modeloPompodekti.ochimaRequired ? 'Το μοντέλο έχει ένδειξη τοποθέτησης σε όχημα υποχρεωτικό ' : undefined) : undefined"
        />
      </div>
      <div v-if="copiedItem.arithmosKykloforiasOchimatos" class="col-4">
        <q-input v-model="validOchima" readonly borderless>
          <template v-slot:prepend>
            <q-icon v-if="validOchima == 'Επαληθεύτηκε'" name="check" color="green" />
            <q-icon v-else name="warning" color="yellow" />
          </template>
        </q-input>
      </div>
    </div>
    <div :key="'arith_' + copiedItem.arithmosKykloforiasOchimatos">
      <ItemOchimaSoap v-model="validOchima" :arithmos-kykloforias-ochimatos="copiedItem.arithmosKykloforiasOchimatos"></ItemOchimaSoap>
    </div>
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <q-input
      filled
      v-model="copiedItem.arithmosDiaylou"
      :label="t('arithmosDiaylou')"
      data-cy="item-arithmosDiaylou"
      clearable
      :readonly="!editable"
      :class="restrictions ? 'required' : ''"
      :rules="restrictions ? requiredRule() : null"
      hide-bottom-space
    />
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <q-checkbox
      left-label
      v-model="copiedItem.isExoplismosLink"
      :label="t('isExoplismosLink')"
      data-cy="item-isExoplismosLink"
      :toggle-indeterminate="false"
      :disable="!editable"
    ></q-checkbox>
  </div>
  <div class="col-12 col-md-12 q-mb-md">
    <SelectEnumById
      :key="idPompodektiKey"
      filled
      v-model="copiedItem.idSyndedemenouPompodekti"
      :label="t('idSyndedemenouPompodekti')"
      data-cy="item-idPompodektiKey"
      clearable
      :readonly="!editable"
      :option-label="'serialNumber'"
      enum-end-point="items/pompodektis"
      :query-params="'draft=false'"
      :class="restrictions ? ((copiedItem.isExoplismosLink) ? 'required' : '') : ''"
      :rules="restrictions ? ((copiedItem.isExoplismosLink) ? requiredRule() : null) : null"
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
      error-message="Aν δεν είναι τοποθετημένο σε όχημα πρέπει να αναφέρετε το λόγο στις παρατηρήσεις"
      :class="restrictions ? ((copiedItem.modeloPompodekti && copiedItem.modeloPompodekti.ochimaRequired && !copiedItem.arithmosKykloforiasOchimatos && !copiedItem.paratiriseis) ? 'required' : '') : ''"
      :rules="restrictions ? ((copiedItem.modeloPompodekti && copiedItem.modeloPompodekti.ochimaRequired && !copiedItem.arithmosKykloforiasOchimatos && !copiedItem.paratiriseis) ? requiredRule() : null) : null"
      hide-bottom-space
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
import { Antivalistiko, Pompodektis } from 'src/services/models';
import InputDate from 'components/inputs/InputDate.vue';
import SelectEnumById from 'components/selects/SelectEnumById.vue';
import TableMetaptosiPompodektis from "components/tables/items/TableMetaptosiPompodektis.vue";
import axios from "axios";
import {useValidationRules} from "src/utils/validation-rules";
import {Id} from "src/services/types";
import ItemOchimaSoap from "components/items/ItemOchimaSoap.vue";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Pompodektis | null;
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
const copiedItem: Pompodektis = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

const idPompodektiKey = ref(0);

const forceRerender = () => {
  idPompodektiKey.value += 1;
};

let metaptosiDialog = ref(false);
let metaptosiSelected = ref([]);

function onMetaptosi() {
  metaptosiDialog.value = false;
  console.log(JSON.stringify(metaptosiSelected.value));
  let selected = metaptosiSelected.value[0];
  axios
    .get('/api/v1/items-metaptosi/pompodektisAnalogikos/dto/' + selected.id)
    .then((res) => {
      // Clear initial values from the item object
      Object.keys(copiedItem.value).forEach((key) => {
        console.log(key)
        if (['serialNumber', 'paratiriseis', 'katastash', 'idMetaptosis', 'arithmosKykloforiasOchimatos',
          'ypokatastasiPompodekti',
          'markaPompodekti',
          'modeloPompodekti',
          'issi',
          'arithmosKykloforiasOchimatos', // Duplicate removed
          'katigoriaAnalogikouPompodekti',
          'arithmosDiaylou',
          'isExoplismosLink',
          'idSyndedemenouPompodekti',
          'markaSintirisiDateRequired',
          'imerominiaSyntirisis'].includes(key)) {
          copiedItem.value[key] = undefined; // or null, or any other appropriate value
        }
      });
      Object.assign(copiedItem.value, res.data);
      console.log(res.data)
      forceRerender()
      //copiedItem.value.paratiriseis = res.data.paratiriseis;
    });
}

function onMetaptosiMultiple() {
  metaptosiDialog.value = false;
  let selected = metaptosiSelected.value.map(mt => mt.id);
  emit('onMetaptosiMultiple', selected)
}

let validOchima = ref('Δεν επαληθεύτηκε');
const { requiredRule } = useValidationRules();
</script>
