<template>
  <!-- pagination caused the table not to show the fetched data-->
  <q-table
    :wrap-cells="true"
    :title="props.title2"
    :columns="columns"
    :rows="applicationTemplates"
    row-key="id"
    selection="multiple"
    v-model:selected="selectedRowsForDeletion"
    @row-click="goToNewEditTemplate"
    :rows-per-page-options="[10, 20, 50]"
  >
    <!-- To add buttons for New/Delete on top of Table-->
    <template v-slot:top-right>
      <q-input dense debounce="300" color="primary" v-model="filter">
        <template v-slot:append>
          <q-icon name="search" />
        </template>
      </q-input>
    </template>
    <!-- Quasar is stupid and links the bottom slot to pagination, so it doesnt show if there are no rows-->
    <!-- So we place two slots -->
    <template v-slot:bottom >
      <q-btn  :label="t('Add')" icon="add" @click="goToNewEditTemplate"  class="bg-dark text-white" unelevated ripple/>
      <q-btn v-if="applicationTemplates?.length !== 0"  :disable="selectedRowsForDeletion?.length == 0" :label="t('Delete')" icon="o_delete" @click="confirmDeletion"  class="q-ml-sm bg-negative text-white" unelevated ripple/>
      <q-space />
    </template>
    <template v-slot:no-data >
      <q-btn  :label="t('Add')" icon="add" @click="goToNewEditTemplate"  class="bg-dark text-white" unelevated ripple/>
      <q-space />
    </template>
  </q-table>

  <!-- Dialog for the New/Edit Template -->
<DialogSealingTemplate v-if="newOrEditDialogIsVisible" v-model:newOrEditDialogIsVisible="newOrEditDialogIsVisible" v-model:editedNewTemplate="editedNewTemplate" @updateTemplate="updateTemplate" />
</template>

<script setup lang="ts">

import { useDateFormatter } from 'src/utils/date-formatter';
import {
  PageResponse, SealingApplication, SealingApplicationClass, SealingTemplate,
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
const $q = useQuasar()

const columns = [
  {
    name: 'Template_Code',
    required: true,
    label: t('Template_Code'),
    align: 'left',
    field: (row: SealingTemplate) => row.code,
  },
  {
    name: 'Template_Description',
    required: true,
    label: t('Template_Description'),
    align: 'left',
    field: (row: SealingTemplate) => row.description
  },
  // {
  //   name: 'Template_File',
  //   required: true,
  //   label: t('Template_File'),
  //   align: 'left',
  //   field: (row: SealingTemplate) => row.fileData?.name,
  // },
  {
    name: 'Template_Μax_Signatures',
    required: true,
    label: t('Template_Μax_Signatures'),
    align: 'left',
    field: (row: SealingTemplate) => row.maxSignatures,
  },
  {
    name: 'Template_Seal_Every_Page',
    required: true,
    label: t('Template_Seal_Every_Page'),
    align: 'left',
    field: (row: SealingTemplate) => row.sealOnEveryPage  ? "Ναι" : "Όχι",
  },
  {
    name: 'Template_Signatures_Metadata',
    required: true,
    label: t('Template_Signatures_Metadata'),
    align: 'left',
    field: (row: SealingTemplate) => row.signaturesMetadata?.map(item => getPositionDescription(item.position)).join(', '),
  },
];

const props = defineProps({
  title2: {
    type: String,
    required: false,
    default: () => null,
  },
  applicationCode: {
    type: String,
    required: true
  },
});


//-------------------------Filter Section---------------------------//
// const filter = ref("");
// function  filterFunction(rows: any, searchTerm: string){
//   const filteredItems = data.value.filter(app => `${app.description} || ${app.challengeChannels?.map(item => item.description).join(', ')} `.toLowerCase().includes(searchTerm.toLowerCase()));
//   return filteredItems;
// }
//-------------------------End of Filter Section---------------------------//
//---------------------Deletion-----------------------------//
//This is a 2-way model between parent and child
const applicationTemplates = defineModel('applicationTemplates', { required: true })


//----------------------For Template editing----------------------------------//
//To open/close the newEditTemplateDialog
const newOrEditDialogIsVisible = ref(false)

//the object that will be passed to the NewEditTemplateDialog
const editedNewTemplate : SealingTemplate = ref({})

//Routes to edit Application
function goToNewEditTemplate(evt: any, row: any) {
  // console.log(evt)
   //console.log(row)
  //To allow editing, this will also be the new Object in case of New Object
  //editedNewTemplate.value = {...row} //This creates a shoallow copy, but arrays are copied by reference and we dont want that in case the user cancels any changes
  editedNewTemplate.value = row ? JSON.parse(JSON.stringify(row)) : {};
  //If we are creating a new Template, set its sealOnEveryPage atribute to false
  if (!editedNewTemplate.value.id) {
    editedNewTemplate.value.sealOnEveryPage = false
    //If it has not signatures value, set it to 1
    if (!editedNewTemplate.value.maxSignatures) {
      editedNewTemplate.value.maxSignatures = 1
    }
    //Set the Template Code according to the Application Code and ascend
    //console.log( applicationTemplates.value)
    //editedNewTemplate.value.code = props.applicationCode?.substring(3) + '_' + Number(applicationTemplates.value.length + 1)
    editedNewTemplate.value.code = t('Automatically_Generated')
  }
  newOrEditDialogIsVisible.value = true
}

//To replace the row with the edited Template
function updateTemplate() {
  // console.log('Updating template')
  // console.log(editedNewTemplate)
  // console.log(applicationTemplates.value)
  //First we find the index of the edited template, using other criteria in case there is no id (user has created more than 1 temp template)
  const index = applicationTemplates.value.findIndex(row => row.id &&  (row.id === editedNewTemplate.value.id) || row.code === editedNewTemplate.value.code || row.description === editedNewTemplate.value.description);
  //If the Template already existed, update it
  if (index !== -1) {
    //console.log('match')
    applicationTemplates.value[index] = editedNewTemplate.value;
  }
  //If it didnt exist, add it
  else {
    applicationTemplates.value.push(editedNewTemplate.value)
    // console.log(applicationTemplates.value)
  }
}

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
    deleteRecords(selectedRowsForDeletion.value?.map(row => row.id))
  })
}

//Actually deletes the records
function deleteRecords(applicationIdsForDeletion: string[]) {
  // Create a Set from the idsToRemove array for efficient lookups
  const idsToRemoveSet = new Set(applicationIdsForDeletion);
  // Filter the targetList to exclude objects with ids in the idsToRemoveSet
  applicationTemplates.value = applicationTemplates.value.filter(template => !idsToRemoveSet.has(template.id));
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



//Dummy data for test
// Create an instance of the class
// const dummyApplication1 = new SealingApplicationClass("1", "206YPA", "Αεροδρόμια", true,[{id: "1", description: 'SMS'},  {id: '2', description: 'Email'}]);
// const dummyApplication2 = new SealingApplicationClass("2", "203SIGNALS", "Σηματικές", false, [{id: "1", description: 'SMS'},  {id:'2', description: 'Email'}]);
// const dummyApplications = ref([dummyApplication1, dummyApplication2])
// const { data, isFetching, refetch } = useQuery({
//   queryKey: ['getSealingApplications', queryPagination],
//   queryFn: () =>
//     getSealingApplications(
//       queryPagination.value.size,
//       queryPagination.value.index
//     ),
//   onSuccess: (data: PageResponse<SealingApplication>) => {
//     tablePagination.value.page = data.index + 1;
//     tablePagination.value.rowsPerPage = data.size !== -1 ? data.size : 0;
//     tablePagination.value.rowsNumber = data.total;
//   },
//   // initialData: () => {
//   //   return {
//   //     content: [],
//   //     //content: dummyApplications,
//   //     contentSize: 0,
//   //     total: 0,
//   //     index: 0,
//   //     size: 0,
//   //   };
//   // },
// });
//
// function onRequest(props: any) {
//   queryPagination.value.size = props.pagination?.rowsPerPage || -1;
//   queryPagination.value.index = props.pagination?.page - 1;
// }
</script>
