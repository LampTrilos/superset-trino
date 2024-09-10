<template>
  <!-- Table that shows the QR, the signautres and their X,Y -->
  <!-- pagination caused the table not to show the fetched data-->
  <q-table
    :wrap-cells="true"
    :title="props.title2"
    :columns="columns"
    :rows="signaturesAndQr"
    row-key="index"
    selection="multiple"
    v-model:selected="selectedRowsForDeletion"
    @row-click="console.log('Editing..')"
    :rows-per-page-options="[10, 20, 50]"
  >
    <template v-slot:body="props">
      <q-tr :props="props">
        <q-td key="Index" :props="props">
          <q-checkbox v-model="props.selected" />
        </q-td>
        <q-td key="Type" :props="props">{{ props.row.index === 0 ? 'QR' : t('Signature') }}</q-td>
        <q-td key="Index" :props="props">
          <div class="text-pre-wrap">{{ props.row.index }}</div>
<!--          <q-popup-edit v-model="props.row.index" v-slot="scope">-->
<!--            <q-input  v-model="scope.value" dense autofocus />-->
<!--          </q-popup-edit>-->
        </q-td>
        <q-td key="Position" :props="props">
          <div class="text-pre-wrap">{{ getPositionDescription(props.row.position) }}</div>
                    <q-popup-edit v-model="props.row.position" v-slot="scope">
                      <SelectGeneric v-model:valuesSelected="scope.value" :devOptions="positionMap"  :fetch-data="false"  @keyup.enter="scope.set" @update:modelValue="customSetter( props.row.index, scope.value)" dense autofocus/>
<!--                      <SelectGeneric  v-model:valuesSelected="scope.value" :devOptions="positionMap" :fetch-data="false" @keyup.enter="scope.set" @update:valuesSelected="scope.set" dense autofocus/>-->
<!--                      <q-select filled v-model="scope.value" :option-value="(opt) => opt.id" :option-label="(opt) => opt.id" :options="positionMap" :fetch-data="false" @keyup.enter="scope.set" @update:modelValue="scope.set" dense autofocus/>-->
                    </q-popup-edit>
        </q-td>
      </q-tr>
    </template>
    <!-- Quasar is stupid and links the bottom slot to pagination, so it doesnt show if there are no rows-->
    <!-- So we place two slots -->
    <template v-slot:bottom >
      <q-btn  :label="t('Add')" :disable="maxSignaturesReached"  icon="add" @click="addNewSignatureOr"  class="bg-dark text-white" unelevated ripple/>
      <q-btn v-if="signaturesAndQr?.length !== 0"  :disable="selectedRowsForDeletion?.length == 0" :label="t('Delete')" icon="o_delete" @click="confirmDeletion"  class="q-ml-sm bg-negative text-white" unelevated ripple/>
      <q-space />
    </template>
    <template v-slot:no-data >
      <q-btn  :label="t('Add')" :disable="maxSignaturesReached" icon="add" @click="addNewSignatureOr"  class="bg-dark text-white" unelevated ripple/>
      <q-space />
    </template>
  </q-table>
</template>

<script setup lang="ts">

import { useDateFormatter } from 'src/utils/date-formatter';
import {
  PageResponse, positionMap, SealingApplication, SealingApplicationClass, SealingTemplate, SignatureMetadata,
} from 'src/services/models';
import {provide, ref} from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';
import {deleteSealingApplications, getSealingApplications} from 'src/services/transactions';

import { useNumberFormatter } from 'src/utils/number-formatter';
const { englishToGreekNumber } = useNumberFormatter();

const { formatDep, formatItemCodeFull, formatUser } = useCodeFormatter();

const { formatDateTimeLocale, formatDate } = useDateFormatter();
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
import { useRouter } from 'vue-router';
const router = useRouter();
import {Notify, useQuasar} from 'quasar'
import DialogSealingTemplate from "components/dialog/DialogSealingTemplate.vue";
import {getPositionDescription} from "src/services/sealposition";
import SelectGeneric from "components/selects/SelectGeneric.vue";
const $q = useQuasar()

const columns = [
  {
    name: 'Type',
    required: true,
    label: t('Type'),
    align: 'center',
    field: (row: SignatureMetadata) => row.index === 0 ? 'QR' : t('Signature') ,
  },
  {
    name: 'Index',
    required: true,
    label: t('Index'),
    align: 'center',
    field: (row: SignatureMetadata) => row.index === 0 ? '-' : row.index,
  },
  {
    name: 'Position',
    required: true,
    label: t('Position')  + ' -  Επιλέξτε για τροποποίηση' ,
    align: 'center',
    field: (row: SignatureMetadata) => getPositionDescription(row.position),
  },
];

const props = defineProps({
  title2: {
    type: String,
    required: false,
    default: () => null,
  },
  //Whether the table already has the max signatures, in order to block the Add Button
  maxSignaturesReached: {
    type: Boolean,
    required: false,
    default: () => false
  }
});

//Couldnt get this stupid quasar q-select to workd with popup edit
function customSetter(indexOfEditedSignature: number, value: string) {
  // console.log(value)
  // console.log(signaturesAndQr.value.find((signature) => signature.index === indexOfEditedSignature))
  let indexOfList = signaturesAndQr.value.findIndex(signature => signature.index === indexOfEditedSignature)
  //console.log(indexOfList)
  signaturesAndQr.value[indexOfList].position = value.id
  //console.log(signaturesAndQr.value[indexOfList])
}

//-------------------------Filter Section---------------------------//
// const filter = ref("");
// function  filterFunction(rows: any, searchTerm: string){
//   const filteredItems = data.value.filter(app => `${app.description} || ${app.challengeChannels?.map(item => item.description).join(', ')} `.toLowerCase().includes(searchTerm.toLowerCase()));
//   return filteredItems;
// }
//-------------------------End of Filter Section---------------------------//
//---------------------Deletion-----------------------------//
//This is a 2-way model between parent and child
const signaturesAndQr = defineModel('signaturesAndQr', { required: true })

//----------------------For Template editing----------------------------------//
//the object that will be passed to the NewEditTemplateDialog
//const editedNewTemplate = ref({})

//Adds new Signatre/QR Metadata
function addNewSignatureOr(evt: any, row: any) {
  // console.log(evt)
  // console.log(row)
  //To allow editing
  //editedNewTemplate.value = {...row}
  //newOrEditDialogIsVisible.value = true
  //We create a new SignatureMetadata object
  let newSignatureQRMetadata : SignatureMetadata = {}
  //Set its index to the lowest possible (index 0 = QR, index 1 = Signature 1)
  //Find the NEXT highest value integer of the property 'index' of a list of object, setting 0 as the value if the list is empty
  let newIndex = -1;
  //If there already are Signature Metadata, and there is QR metadata find the next highest number, otherwise set the QR (Index 0)
  if (signaturesAndQr.value && signaturesAndQr.value.length !== 0 && signaturesAndQr.value.some(obj => obj.index === 0)) {
    newIndex =  signaturesAndQr.value.reduce((max, obj) => {
      return obj.index > max ? obj.index : max;
    }, -Infinity);
  }
  //If the array of signatureMetadata is empty
  else {
    signaturesAndQr.value = []
  }
  newSignatureQRMetadata.index = newIndex + 1
  newSignatureQRMetadata.position = '01'
  setTimeout(() => {
    signaturesAndQr.value.push(newSignatureQRMetadata)
  }, 50);

}

//To replace the row with the edited Template
// function updateSignature() {
//   console.log("Updated Signature")
//   console.log(editedNewTemplate)
//   //First we find the index of the edited template, using other criteria in case there is no id (user has created more than 1 temp template)
//   const index = applicationTemplates.value.findIndex(row => row.id === editedNewTemplate.id || row.code === editedNewTemplate.code || row.description === editedNewTemplate.description);
//   if (index !== -1) {
//     applicationTemplates.value[index] = editedNewTemplate;
//   }
// }

//-------------------End of Template Editing--------------------------------//


//Selected rows for Deletion
const selectedRowsForDeletion = ref([])
//Confirm Dialog for Deletion, sends an array of the application Ids to be deleted
function confirmDeletion () {
  $q.dialog({
    title: 'Επιβεβαίωση διαγραφής',
    message: 'Είστε σίγουροι για τη διαγραφή;',
    cancel: true,
    persistent: true
  }).onOk(() => {
    deleteRecords(selectedRowsForDeletion.value?.map(row => row.index))
  })
}

//Actually deletes the records
function deleteRecords(applicationIdsForDeletion: string[]) {
  // Create a Set from the idsToRemove array for efficient lookups
  const idsToRemoveSet = new Set(applicationIdsForDeletion);
  // Filter the targetList to exclude objects with indices in the idsToRemoveSet
  signaturesAndQr.value = signaturesAndQr.value.filter(row => !idsToRemoveSet.has(row.index));
  //Code was too fast, so we need to wait for the filter to work before proceeding
  setTimeout(() => {
    console.log(signaturesAndQr.value)
    //If after the deletion there are number missing (index 0,1,5,7), edit the rows so that there no gaps
    signaturesAndQr.value.sort((a, b) => a.index - b.index); // Sort the array by the index property
    signaturesAndQr.value.forEach((obj, i) => {
      obj.index = i; // Reassign the index to be sequential
  }, 100);

  });
  return ;
}

//---------------------End of Deletion-----------------------------//
let tablePagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
});

let queryPagination = ref({
  size: tablePagination.value.rowsPerPage,
  index: tablePagination.value.page - 1,
});

</script>
