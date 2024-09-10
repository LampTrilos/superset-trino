<template>
  <q-file
    v-model="selectedFiles"
    :label="t('uploadInstructions')"
    filled
    use-chips
    multiple
    text-area
    :readonly="isUploading > 0"
    :accept="accept"
  >
    <template #after>
      <q-btn
        color="primary"
        dense
        icon="upload"
        round
        @click="uploadFiles"
        :loading="isUploading > 0"
        ><q-tooltip>{{ t('upload', 2) }}</q-tooltip></q-btn
      >
    </template>
  </q-file>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import axios from 'axios';
import { useI18n } from 'vue-i18n';
import { useQueryClient } from '@tanstack/vue-query';
import { Id } from 'src/services/types';
import { Notify } from 'quasar';

const { t } = useI18n();

const props = defineProps<{
  url: string;
  queryKey: string;
  id: Id;
  accept?: string;
}>();

let selectedFiles = ref([]);
let isUploading = ref(0);
let progressInfos = reactive([]);
let message = ref('');

const queryClient = useQueryClient();
function upload(idx, file) {
  progressInfos[idx] = { percentage: 0, fileName: file.name };

  uploadFile(props.url, file, (event) => {
    progressInfos[idx].percentage = Math.round(
      (100 * event.loaded) / event.total
    );
  })
    .then((response) => {
      console.log(response);
      if (response.status === 204) {
        console.log('updated successfully ' + idx);
        queryClient.invalidateQueries({ queryKey: [props.queryKey, props.id] });
        selectedFiles.value.splice(idx, 1);
      } else {
        let prevMessage = message.value ? message.value + '\n' : '';
        message.value = prevMessage + response.data.message;
      }
    })
    .then(() => {
      console.log('successfully uploaded');
    })
    .finally(() => isUploading.value--)
    .catch(() => {
      progressInfos[idx].percentage = 0;
      let prevMessage = message.value ? message.value + '\n' : '';
      message.value = prevMessage + 'Could not upload the file:' + file.name;
    });
}

function uploadFiles() {
  console.log('upload');
  message.value = '';
  isUploading.value = selectedFiles.value.length;
  for (let i = 0; i < selectedFiles.value.length; i++) {
    upload(i, selectedFiles.value[i]);
  }
}

const emit = defineEmits(['fileUploaded']);
function uploadFile(url, file, onUploadProgress) {
  let formData = new FormData();
  formData.append('file', file);
  formData.append('name', file.name);
  formData.append('type', file.type);
  formData.append('size', file.size);
  return axios
    .post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data; charset=UTF-8',
      },
      onUploadProgress,
    })
    .then((response) => {
      console.log('Request successful!');
      console.log('Response data:', response.data);
      queryClient.invalidateQueries({ queryKey: [props.queryKey, props.id] });
      Notify.create({
        message: t('uploadSuccess'),
      });
      emit('fileUploaded', response.data);
      // Print anything else you want based on the response data
    })
    .catch((error) => {
      console.error('Error occurred:', error);
      // Handle error cases if needed
    });
}
</script>
