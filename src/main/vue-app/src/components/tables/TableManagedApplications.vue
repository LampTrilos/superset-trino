<template>
  <!-- pagination caused the table not to show the fetched data-->
  <q-table
    :wrap-cells="true"
    bordered
    :columns="columns"
    :rows="data"
    row-key="id"
    @row-click="goToEdit"
    selection="multiple"
    v-model:selected="selectedRowsForDeletion"
    :filter="filter"
    :filter-method="filterFunction"
    :rows-per-page-options="[10, 20, 50]"
    @request="onRequest"
    :loading="isFetching"
  >
    <!-- To add buttons for New/Delete on top of Table-->
    <template v-slot:top-right>
      <q-input dense debounce="300" color="primary" v-model="filter" :label="t('search')" >
        <template v-slot:append>
          <q-icon name="search" />
        </template>
      </q-input>
    </template>
    <template v-slot:bottom>
      <q-btn :disable="isFetching" :label="t('Add')" icon="add" @click="goToNewApplication"  class="bg-dark text-white" unelevated ripple/>
      <q-btn v-if="data?.length !== 0"  :disable="isFetching || selectedRowsForDeletion?.length == 0" :label="t('Delete')" icon="o_delete" @click="confirmDeletion"  class="q-ml-sm bg-negative text-white" unelevated ripple/>
      <q-space />
    </template>
    <!-- So that each row is a link to edit the Row-->
<!--    <template #body-cell-id="propsBody">-->
<!--      <q-td :props="propsBody">-->
<!--        <router-link-->
<!--          class="text-primary"-->
<!--          :to="{-->
<!--            name: 'EditApplication',-->
<!--            params: { id: propsBody.row.id },-->
<!--            query: { type: propsBody.row.id },-->
<!--          }"-->
<!--        >{{ propsBody.value }}-->
<!--        </router-link>-->
<!--      </q-td>-->
<!--    </template>-->
  </q-table>
</template>

<script setup lang="ts">

import { useI18n } from 'vue-i18n';
import { useDateFormatter } from 'src/utils/date-formatter';
import {PageResponse, SealingApplication, SealingApplicationClass,
} from 'src/services/models';
import {provide, ref} from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { useCodeFormatter } from 'src/utils/code-formatter';
import {deleteSealingApplications, getSealingApplications} from 'src/services/transactions';

import { useNumberFormatter } from 'src/utils/number-formatter';
const { englishToGreekNumber } = useNumberFormatter();

const { formatDep, formatItemCodeFull, formatUser } = useCodeFormatter();

const { formatDateTimeLocale, formatDate } = useDateFormatter();
const { t } = useI18n();
import { useRouter } from 'vue-router';
const router = useRouter();
import {Notify, useQuasar} from 'quasar'
const $q = useQuasar()



const columns = [
  {
    name: 'Application_Name',
    required: true,
    label: t('Application_Name'),
    align: 'left',
    field: (row: SealingApplication) => row.description,
  },
  {
    name: 'ChallengeChannels',
    required: true,
    label: t('ChallengeChannels'),
    align: 'left',
    field: (row: SealingApplication) => row.challengeChannels?.map(item => item.description).join(', '),
  },
  {
    name: 'Active',
    required: true,
    label: t('Active'),
    align: 'left',
    field: (row: SealingApplication) => row.active? "Ναι" : "Όχι",
  },
  // {
  //   name: 'from',
  //   required: true,
  //   label: t('from'),
  //   align: 'left',
  //   field: (row: CompoundTransaction) =>
  //     (row.fromAccountEntity
  //       ? '(' +
  //       formatDep(row.fromAccountEntity?.code) +
  //       ') ' +
  //       (row.fromAccountEntity?.name ?? '')
  //       : '') +
  //     (row.fromOu ? row.fromOu.name + ' - ' : '') +
  //     (row.fromUser ? formatUser(row.fromUser) : ''),
  // },
];

const props = defineProps({
  itemId: {
    type: String,
    required: false,
    default: null,
  },
});

//Routes to edit Application
function goToEdit(evt: any, row: any) {
  // console.log(evt)
  // console.log(row)
  router.push({ name: 'ApplicationAddEdit', params: { id: row.id } })
}
//Routes to New Application
function goToNewApplication() {
  //Id = 'new' signals that we are creating, not editing
  router.push({ name: 'ApplicationAddEdit', params: { id: 'new' } })
}
//-------------------------Filter Section---------------------------//
const filter = ref("");
function  filterFunction(rows: any, searchTerm: string){
  const filteredItems = data.value.filter(app => `${app.description} || ${app.challengeChannels?.map(item => item.description).join(', ')} `.toLowerCase().includes(searchTerm.toLowerCase()));
  return filteredItems;
}
//-------------------------End of Filter Section---------------------------//
//---------------------Deletion-----------------------------//
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
async function deleteRecords(applicationIdsForDeletion: string[]) {
  await deleteSealingApplications(applicationIdsForDeletion).then((res: any) => {
    console.log(res.status)
    if (res.status === 200) {
      Notify.create({
        message: t('success'),
        color: 'green'
      });
    }
    else if (res.status === 403) {
      Notify.create({
        message: t('Child_Records_Found'),
        color: 'yellow'
      });
    }
    else {
      Notify.create({
        message: t('failed'),
        color: 'red'
      });
    }
    //When we have a response, refresh the table
    refetch()
    selectedRowsForDeletion.value = []
  })
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
const { data, isFetching, refetch } = useQuery({
  queryKey: ['getSealingApplications', queryPagination],
  queryFn: () =>
    getSealingApplications(
      queryPagination.value.size,
      queryPagination.value.index
    ),
  onSuccess: (data: PageResponse<SealingApplication>) => {
    tablePagination.value.page = data.index + 1;
    tablePagination.value.rowsPerPage = data.size !== -1 ? data.size : 0;
    tablePagination.value.rowsNumber = data.total;
  },
  // initialData: () => {
  //   return {
  //     content: [],
  //     //content: dummyApplications,
  //     contentSize: 0,
  //     total: 0,
  //     index: 0,
  //     size: 0,
  //   };
  // },
});

function onRequest(props: any) {
  queryPagination.value.size = props.pagination?.rowsPerPage || -1;
  queryPagination.value.index = props.pagination?.page - 1;
}
</script>
