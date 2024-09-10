<template>
  <h5>{{ isNew ? t('add') : t('edit') }} {{ t('Application', 2) }}</h5>
    <q-inner-loading
      v-if="isFetching"
      :showing="isFetching"
      :label="t('Please_wait')"
      label-class="text-teal"
      label-style="font-size: 1.1em"
    />
  <div v-else-if="isError">Error: {{ error.message }}</div>
  <div v-else-if="applicationEdited">
    <q-form @submit="onSubmit">
      <div class="row q-col-gutter-sm">
        <div class="row col-12 justify-center q-mb-md">
        <div class="col-4 q-mr-sm">
          <!-- Either pick or show based on isNew -->
          <SelectGeneric v-if="isNew" :label="t('Application_Name', 0)" v-model:valuesSelected="applicationChosen" :devOptions="devListOfApps"  :fetchData="false" fetchUrl="/api/v1/fechAllApplicationsDADSADS" :rules="requiredRule()" class="q-mb-sm"/>
          <q-input
            v-else
            filled
            autogrow
            v-model="applicationEdited.description"
            :label="t('Application_Name')"
            :rules="requiredRule()"
            clearable
            disable
            class="q-mb-sm "
          />
          <q-input
            filled
            autogrow
            v-model="applicationEdited.appCode"
            :label="t('Application_Code')"
            :rules="requiredRule()"
            clearable
            disable
            class="q-mb-sm"
          />
        </div>
          <div class="col-4">
          <SelectGeneric :label="t('ChallengeChannels', 0)" v-model:valuesSelected="applicationEdited.challengeChannels" fetchUrl="/api/v1/challenge-channels/getAllChannels" :rules="requiredRule()" multiple use-chips/>
          <q-toggle
            v-model="applicationEdited.active"
            :label="t('Active') + ' : ' + (applicationEdited.active ? 'Ναι' : 'Όχι')"
            :rules="requiredRule()"
            left-label
            color="primary"
            keep-color
            class="q-mb-sm q-mt-sm q-pl-sm"
          />
          </div>
        </div>
        </div>
      <!-- Table that shows an Application's Templates, also contains Add/Edit Template Functionality -->
      <div class="col-12 col-md-4">
<!--        {{applicationEdited.templates?.length}}-->
        <TableApplicationTemplates :title2="t('Application_Templates')" v-model:applicationTemplates="applicationEdited.templates" :applicationCode="applicationEdited.appCode" :rules="requiredRule()"/>
      </div>

      <div class="row col-12 justify-center q-mt-lg">
        <q-btn
          :label="t('save')"
          type="submit"
          color="primary"
          no-caps
          unelevated
          class="q-mr-sm"
        />
        <q-btn
          :label="t('back')"
          outline
          no-caps
          :to="{ name: 'ManageApplications' }"
        />
      </div>
    </q-form>
    <!-- Alerts if user tries to leave with unsaved changes -->
    <AlertUnsavedChanges
      v-if="!isNew"
      :object-to-watch="applicationEdited"
      class="q-mt-xl"
    />
  </div>
</template>

<script setup async lang="ts">
import {reactive, ref, computed, watch} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  getSealingApp, getSealingAppById, insertOrUpdateSealingApp,
} from '../../services/transactions';
import { useBreadcrumbsStore } from 'stores/breadcrumbs';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import { Id } from 'src/services/types';
import {
  ChallengeChannel,
  Enum,
  SealingApplication,
  PageResponse,
  ITApplication,
  SealingTemplateClass
} from 'src/services/models';
import { Loading, Notify } from 'quasar';
import AlertUnsavedChanges from 'components/alerts/AlertUnsavedChanges.vue';
import SelectGeneric from "components/selects/SelectGeneric.vue";
import TableApplicationTemplates from "components/tables/TableApplicationTemplates.vue";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
const route = useRoute();
import { useValidationRules } from 'src/utils/validation-rules';
const { requiredRule } = useValidationRules();

const id: Id = route.params.id;
//To check if we are creating or editing
const isNew = computed(() => { return id === 'new' })

//The Application Object currently managed on screen
let applicationEdited = reactive<SealingApplication>({
  id: null,
  appCode: null,
  description: null,
  active: false,
  challengeChannels: null,
  templates: []
});

//We watch the applicationChosen for any changes via the SelectGeneric, and modify the applicationEdited accordingly
const applicationChosen = ref(null)
watch(
  applicationChosen,
  (newVal, oldVal) => {
    applicationEdited.appCode = applicationChosen.value.id
    applicationEdited.description = applicationChosen.value.description
    //console.log('User object changed:', { newVal, oldVal });
  },
  { deep: true } // This option is necessary for watching nested properties
);

//Fetches the row to be edited if the id of the "edited" row is not 'new', i.e we are editing, not creating
const { isFetching, isError, error } = useQuery({
  queryKey: ['getSealingApp', id],
  queryFn: () => getSealingAppById(id),
  enabled: !isNew.value,
  onSettled: (res) => {
     const breadcrumbsStore = useBreadcrumbsStore();
     breadcrumbsStore.setDynamicPropertyToLastBreadcrumb(res?.description);
    Object.assign(applicationEdited, res);

    console.log(applicationEdited)
    console.log(id)
  },
});

const queryClient = useQueryClient();
const router = useRouter();


const { mutate: postM } = useMutation(insertOrUpdateSealingApp, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['getSealingApp'] });
    Notify.create({
      message: t('saveSuccess'),
      color: 'green'
    });
    router.push({ name: 'ManageApplications' });
  },
  onSettled: () => {
    Loading.hide();
  },
});

function onSubmit() {
  postM(applicationEdited);
}
//------------------------------Table of Templates Section-------------------------------
//We watch the applicationChosen for any changes via the SelectGeneric, and modify the applicationEdited accordingly

//-----------------For Dev while we still dont have a list of Applications---------------------------//
const devListOfApps : ITApplication[] = ref (
  [
    {
      id: '143ALLILOG',
      description: 'Αλληλογραφία',
    },
    {
      id: '212PANIC',
      description: 'Panic Button',
    }
  ]
)

//------------------------------------------------------------------------//
</script>
