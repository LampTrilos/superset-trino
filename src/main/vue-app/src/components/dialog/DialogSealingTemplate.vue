<template>
<!-- this Dialog creates/edits a tamplte for an Application--->
  <q-dialog full-width v-model="newOrEditDialogIsVisible">
    <div>
      <!-- QForm cant be a direct child of q-dialog, it needs a parent div, stupid Quasar -->
    <q-form @submit="onSubmit">
    <q-card >
      <q-card-section>
        <h5>{{t('Template_Edit')}}</h5>
        <div class="row q-col-gutter-sm">
          <div class="row col-12 justify-center q-mb-md">
            <div class="col-4 q-mr-sm">
              <!-- Either pick or show based on isNew -->
              <!--                  <SelectGeneric v-if="isNew" :label="t('Application_Name', 0)" v-model:valuesSelected="applicationChosen" :devOptions="devListOfApps"  :fetchData="false" fetchUrl="/api/v1/fechAllApplicationsDADSADS" class="q-mb-sm"/>-->
              <q-input
                filled
                autogrow
                v-model="editedNewTemplate.description"
                :label="t('Template_Description')"
                clearable
                class="q-mb-sm"
                lazy-rules
                :rules="[
                  val => (val && val !== '') || t('Generic_Error'),
                ]"
              />
              <q-input
                filled
                autogrow
                v-model="editedNewTemplate.code"
                :label="t('Template_Code')"
                clearable
                class="q-mb-sm "
                :rules="[
                  val => (val && val !== '') || t('Generic_Error'),
                ]"
                disable
              />
            </div>
            <div class="col-4">
              <!--                  <SelectGeneric :label="t('ChallengeChannels', 0)" v-model:valuesSelected="editedNewTemplate.challengeChannels" fetchUrl="/api/v1/challenge-channels/getAllChannels" multiple use-chips/>-->
              <q-input
                v-model.number="editedNewTemplate.maxSignatures"
                :label="t('Template_Μax_Signatures')"
                type="number"
                filled
                left-label
                color="primary"
                keep-color
                lazy-rules
                :rules="[
          val => (val > 0 && val < 10) || t('Generic_Error'),
        ]"
              />
              <q-toggle
                v-model="editedNewTemplate.sealOnEveryPage"
                :label="t('Template_Seal_Every_Page') + ' : ' + (editedNewTemplate.sealOnEveryPage ? 'Ναι' : 'Όχι')"
                left-label
                color="primary"
                keep-color
                class=" q-mt-sm q-pl-sm"
              />
            </div>
            <div class="col-4 q-mr-sm">
              <!-- Either pick or show based on isNew -->
              <!--                  <SelectGeneric v-if="isNew" :label="t('Application_Name', 0)" v-model:valuesSelected="applicationChosen" :devOptions="devListOfApps"  :fetchData="false" fetchUrl="/api/v1/fechAllApplicationsDADSADS" class="q-mb-sm"/>-->
              <q-input
                type="textarea"
                filled
                autogrow
                v-model="editedNewTemplate.emailMessage"
                :label="t('Message_Content')"
                hint="Στο τέλος του μηνύματος θα προστεθεί το μοναδικό αναγνωριστικό του εγγράφου που υπογράφετε"
                clearable
                class="q-mb-sm"
                lazy-rules
                :rules="[
                  val => (val && val !== '') || t('Generic_Error'),
                ]"
              />
            </div>
          </div>
<!------------------------- For file upload, not to be used at first ------------------------->
<!--          <div class="row col-12 justify-center">-->
<!--            <q-file filled bottom-slots v-model="editedNewTemplate.fileData" :label="t('Template_File')" accept=".pdf"  max-file-size="10048000" @rejected="onRejectedFile" counter>-->
<!--              <template v-slot:prepend>-->
<!--                <q-icon name="cloud_upload" @click.stop.prevent />-->
<!--              </template>-->
<!--              <template v-slot:append>-->
<!--                <q-icon name="close" @click.stop.prevent="editedNewTemplate.fileData = null" class="cursor-pointer" />-->
<!--              </template>-->
<!--            </q-file>-->
<!--          </div>-->
        </div>
      </q-card-section>
<!--      <q-card-section>-->
<!--           Alerts if user tries to leave with unsaved changes -->
<!--          <AlertUnsavedChanges-->
<!--            v-if="!isNew"-->
<!--            :object-to-watch="applicationEdited"-->
<!--            class="q-mt-xl"-->
<!--          />-->
<!--      </q-card-section>-->

      <q-card-section class="q-pt-none">
        <!-- Table that shows an Application's Templates -->
        <div class="col-12 col-md-4">
          <!--        {{applicationEdited.templates?.length}}-->
          <TableSignatureMetadata :title2="t('Signatures_QR')" v-model:signaturesAndQr="editedNewTemplate.signaturesMetadata" :maxSignaturesReached="maxSignaturesReached" />
        </div>
      </q-card-section>

      <q-separator />
      <q-card-actions align="right">
        <div class="row col-12 justify-center q-mt-lg">
          <q-btn
            :label="t('Temp_Save')"
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
            @click="cancel"
          />
        </div>
      </q-card-actions>
    </q-card>
    </q-form>
    </div>
  </q-dialog>
</template>
<script setup lang="ts">
import TableApplicationTemplates from "components/tables/TableApplicationTemplates.vue";
import AlertUnsavedChanges from "components/alerts/AlertUnsavedChanges.vue";
import SelectGeneric from "components/selects/SelectGeneric.vue";
import {computed, reactive, ref, onMounted, onUnmounted} from "vue";
//import { SealingTemplate } from "src/services/models";
import { useI18n } from 'vue-i18n';
import TableSignatureMetadata from "components/tables/TableSignatureMetadata.vue";
import {Notify} from "quasar";
const { t } = useI18n();
const emit = defineEmits(['updateTemplate'])


//This is a 2-way model between parent and child to open/close the Dialog
const newOrEditDialogIsVisible = defineModel('newOrEditDialogIsVisible', { required: true })
//This is a 2-way model between parent and child for the New/Edited Template
const editedNewTemplate = defineModel('editedNewTemplate', { required: true })
//We want to preserve the snapshot of the edited item, so if the user hits 'Back' the changes go away
// const editedNewTemplateOriginal = ref(null)
// onMounted(() => {
//   // console.log('Filling values')
//   // console.log(editedNewTemplate.value)
//    //console.log({...editedNewTemplate.value})
//   editedNewTemplateOriginal.value = JSON.parse(JSON.stringify(editedNewTemplate.value))//;{...editedNewTemplate.value}
//   //console.log(editedNewTemplateOriginal.value)
//   //console.log(editedNewTemplateOriginal.value)
// })


//Props
// const props = defineProps<{
//   id: string ;
// }>();
const isNew = computed(() => { return editedNewTemplate.value.id === 'new' })

//Whether the table already has the max signatures or no signatures declared, in order to block the Add Button
const maxSignaturesReached = computed(() => {
  // console.log(editedNewTemplate.value.signaturesMetadata.length)
  // console.log(editedNewTemplate.value.maxSignatures + 1)
  return editedNewTemplate.value.signaturesMetadata && (!editedNewTemplate.value.maxSignatures || editedNewTemplate.value.signaturesMetadata.length >= Number(editedNewTemplate.value.maxSignatures) + 1)
})

//The Application Object currently managed on screen
// let applicationEdited = reactive<SealingApplication>({
//   id: null,
//   appCode: null,
//   description: null,
//   active: false,
//   challengeChannels: null,
//   templates: null
// });


//It signals to the parent (TableApplicationTemplates) that a Creation/Update has taken place
function onSubmit() {
  emit('updateTemplate', null)
  //console.log(editedNewTemplate.value)
  //editedNewTemplate.value.signaturesMetadata = []
  newOrEditDialogIsVisible.value = false
  //postM(applicationEdited);
}

//Closes the dialog and reset the edited Object to its original state
function cancel() {
   //console.log(editedNewTemplateOriginal.value)
   //editedNewTemplate.value = JSON.parse(JSON.stringify(editedNewTemplateOriginal.value))//{...editedNewTemplateOriginal.value}
   //editedNewTemplate.value = editedNewTemplateOriginal.value//{...editedNewTemplateOriginal.value}
 // editedNewTemplate.value.signaturesMetadata = editedNewTemplateOriginal.value.signaturesMetadata
   //console.log(editedNewTemplate.value)
  newOrEditDialogIsVisible.value = false
}
//
//For File upload, not to be used right at the start
// //If a user uploads a forbidden file
// function onRejectedFile () {
//   Notify.create({
//     message: t('File_Not_Accepted'),
//     color: 'negative',
//   });
// }
</script>
