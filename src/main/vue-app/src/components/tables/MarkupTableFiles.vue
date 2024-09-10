<template>
  <q-card flat bordered v-if="files && files.length > 0">
    <q-card-section>
      <q-markup-table flat v-if="files && files.length > 0">
        <thead>
          <tr>
            <th class="text-left">{{ t('name', 0) }}</th>
            <th class="text-left">{{ t('type', 0) }}</th>
            <th class="text-left">{{ t('crTmp', 0) }}</th>
            <th class="text-left"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="file in files" :key="'file-' + file">
            <td>
              {{ file.name }}
            </td>
            <td>
              {{ file.type?.description ?? t('otherFile') }}
            </td>
            <td>
              {{ formatDateTimeLocale(file.createdTimestamp) }}
            </td>
            <td>
              <q-btn
                round
                flat
                size="sm"
                icon="mdi-file-search-outline"
                @click="onPreview(file)"
                ><q-tooltip>{{ t('preview') }}</q-tooltip></q-btn
              >
              <q-btn
                round
                flat
                size="sm"
                icon="download"
                @click="onDownload(file)"
                ><q-tooltip>{{ t('download') }}</q-tooltip></q-btn
              >
              <q-btn
                v-if="editable && !file.type?.generated"
                round
                flat
                size="sm"
                icon="delete"
                @click="openDialog(onDelete, file, 'delete')"
                ><q-tooltip>{{ t('delete') }}</q-tooltip></q-btn
              >
            </td>
          </tr>
        </tbody>
      </q-markup-table>
    </q-card-section>
  </q-card>

  <q-dialog v-model="confirmDialog.open" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">{{ t('doYouConfirmThisAction') }}</div>
      </q-card-section>
      <q-card-section class="column items-center">
        <div class="q-mx-auto">File: {{ confirmDialog.item.name }}</div>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn :label="t('cancel')" outline v-close-popup no-caps />
        <q-btn
          flat
          :label="t(confirmDialog.actionName)"
          color="primary"
          @click="confirmDialog.action(confirmDialog.item)"
          v-close-popup
          no-caps
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <span v-if="isError">{{ t('error') }}: {{ error.message }}</span>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import { Loading, Notify } from 'quasar';
import { useDateFormatter } from 'src/utils/date-formatter';
const { formatDateTimeLocale } = useDateFormatter();
const { t } = useI18n();

const props = defineProps({
  fileTypes: {
    type: Array,
  },
  id: {
    type: String,
  },
  queryKey: {
    type: String,
    required: true,
  },
  getRowsFunction: {
    type: Function,
    required: true,
  },
  deleteRowFunction: {
    type: Function,
    required: false,
    default: () => Promise.resolve(),
  },
  downloadRowFunction: {
    type: Function,
    required: false,
    default: () => Promise.resolve(),
  },
  previewRowFunction: {
    type: Function,
    required: false,
    default: () => Promise.resolve(),
  },
  editable: {
    type: Boolean,
    required: false,
    default: true,
  },
});

const {
  isError,
  data: files,
  error,
} = useQuery({
  queryKey: [props.queryKey, props.id],
  queryFn: () => props.getRowsFunction(props.id),
  select: (data) => data.content,
  onSettled: (res) => {
    emit(
      'update:fileTypes',
      res.map((file) => file.type?.id)
    );
  },
});

function onDownload(item) {
  console.log('[Download] Item: ' + item.name);
  props.downloadRowFunction(props.id, item.id, item.name);
}

function onPreview(item) {
  console.log('[Preview] Item: ' + item.name);
  props.previewRowFunction(props.id, item.id, item.name);
}

const confirmDialog = ref({
  open: false,
  action: null,
  item: {},
  actionName: 'Submit',
});

function openDialog(action, row, actionName: string) {
  console.log('[Open dialog] Item : ' + row);
  confirmDialog.value.open = true;
  confirmDialog.value.item = row;
  confirmDialog.value.action = action;
  confirmDialog.value.actionName = actionName;
}

function onDelete(item) {
  console.log('[Delete] Item: ' + item.name);
  deleteItemM({ id: props.id, fileId: item.id });
}

const queryClient = useQueryClient();
const { mutate: deleteItemM } = useMutation(props.deleteRowFunction, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: [props.queryKey, props.id] });
    Notify.create({
      message: t('deleteSuccess'),
    });
  },
  onSettled: () => {
    Loading.hide();
  },
});

const emit = defineEmits(['update:fileTypes']);
</script>
