<template>
  <h5>{{ t('edit') }} {{ t('announcement', 1) }}</h5>
  <q-inner-loading v-if="isFetching" :showing="isFetching">
    <q-spinner-gears size="50px" color="primary" />
  </q-inner-loading>
  <div v-else-if="isError">Error: {{ error.message }}</div>
  <div v-else-if="announcement">
    <q-form @submit="onSubmit">
      <div class="row q-col-gutter-sm">
        <div class="col-12 col-md-6">
          <q-input
            filled
            autogrow
            v-model="announcement.message"
            label="Κείμενο"
            type="textarea"
            clearable
          />
        </div>
      </div>

      <div class="q-mt-lg">
        <q-btn
          :label="t('save')"
          type="submit"
          color="primary"
          no-caps
          unelevated
          class="q-mr-sm"
        />
      </div>
    </q-form>
  </div>
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import { reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getAnnouncement, postAnnouncement } from '../../services/announements';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import { Announcement } from 'src/services/models';
import { Loading, Notify } from 'quasar';
import AlertUnsavedChanges from 'components/alerts/AlertUnsavedChanges.vue';
import { useCodeFormatter } from 'src/utils/code-formatter';

const { formatDep } = useCodeFormatter();

const { t } = useI18n();

const route = useRoute();

let announcement = reactive<Announcement>({
  id: null,
  message: null,
});

const { isFetching, isError, error } = useQuery({
  queryKey: ['announcements'],
  queryFn: () => getAnnouncement(),
  onSettled: (res) => {
    Object.assign(announcement, res);
  },
});

const queryClient = useQueryClient();
const router = useRouter();
const { mutate: postAnnouncementM } = useMutation(postAnnouncement, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['announcements'] });
    Notify.create({
      message: t('saveSuccess'),
    });
  },
  onSettled: () => {
    Loading.hide();
  },
});

function onSubmit() {
  postAnnouncementM(announcement);
}
</script>
