<template>
  <h5>{{ t('notification', 2) }}</h5>
  <div v-if="announcement?.message" class="row q-my-md q-col-gutter-sm">
    <q-banner rounded class="bg-primary text-white">
      <template v-slot:avatar>
        <q-icon name="sym_o_campaign" />
      </template>
      {{ announcement.message }}
    </q-banner>
  </div>
  <div class="row q-my-md q-col-gutter-sm">
    <div class="col-6 col-md-3">
      <q-select
        filled
        :label="t('status')"
        clearable
        emit-value
        map-options
        v-model.trim="queryParams.read"
        @keydown.enter="onEnterKeySearch"
        :options="[
          {
            label: 'Διαβάστηκε',
            value: 'true',
          },
          {
            label: 'Δεν διαβάστηκε',
            value: 'false',
          },
        ]"
      >
      </q-select>
    </div>
    <div class="col-12 col-md-3">
      <ButtonSearch
        ref="buttonSearch"
        :query-params="queryParams"
        class="full-height"
      />
      <ButtonSearchReset class="full-height" />
    </div>
  </div>
  <TableGenericRouter
    :query-key="'getNotificationByQuery'"
    :columns="columns"
    :get-rows-function="getNotificationByQuery"
    :show-delete-action="false"
    :actions="[
      {
        text: t('markAsRead'),
        icon: 'notifications_off',
        method: 'markAsRead',
        attribute: {
          column: (row: Notification) => row.read,
          value: false,
        },
      },
      {
        text: t('markAsUnRead'),
        icon: 'notifications_on',
        method: 'markAsUnRead',
        attribute: {
          column: (row: Notification) => row.read,
          value: true,
        },
      }
    ]"
    @markAsRead="markAsRead"
    @markAsUnRead="markAsUnRead"
    :hide-export-button="true"
  />
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { Notification } from 'src/services/models';
import { useDateFormatter } from 'src/utils/date-formatter';
import {
  getNotificationByQuery,
  markNotificationAsRead,
  markNotificationAsUnread,
} from 'src/services/notification';
import { useMutation, useQueryClient } from '@tanstack/vue-query';
import TableGenericRouter from 'components/tables/TableGenericRouter.vue';
import { useRoute } from 'vue-router';
import { reactive, ref } from 'vue';
import ButtonSearch from 'components/buttons/ButtonSearch.vue';
import ButtonSearchReset from 'components/buttons/ButtonSearchReset.vue';
import axios from "axios";

const { formatDateTimeLocale } = useDateFormatter();

const { t } = useI18n();

const route = useRoute();
let queryParams = reactive({
  read: null,
  ...route.query,
});

const buttonSearch = ref(null);
function onEnterKeySearch() {
  buttonSearch.value.triggerSearch();
}

const columns = [
  {
    name: 'message',
    required: true,
    label: t('message', 0),
    align: 'left',
    field: (row) => (row.message + (row.attributes?.compTransactionType ? ' ' + t('transaction-' + row.attributes.compTransactionType.toLowerCase()) : '')),
    classes: (row: Notification) => (row.read ? 'light-dimmed-2' : ''),
    link: (row: Notification) => {
      return {
        name: row.attributes.myCompoundTransaction
          ? 'MyCompoundTransactionSign'
          : 'CompoundTransactionSign',
        params: {
          id: row.attributes.compTransactionId,
        },
        query: { type: row.attributes.compTransactionType },
      };
    },
  },
  {
    name: 'createdTimestamp',
    required: true,
    label: t('crTmp'),
    align: 'left',
    classes: (row: Notification) => (row.read ? 'light-dimmed-2' : ''),
    field: (row: Notification) => formatDateTimeLocale(row.createdTimestamp),
    serverSideSorting: false,
  },
  {
    name: 'actions',
    classes: (row: Notification) => (row.read ? 'light-dimmed-2' : ''),
    label: t('actions'),
  },
];

const queryClient = useQueryClient();
const { mutate: markNotificationAsReadM } = useMutation(
  markNotificationAsRead,
  {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['getNotifications'] });
      queryClient.invalidateQueries({ queryKey: ['getNotificationByQuery'] });
    },
  }
);

function markAsRead(row: Notification) {
  markNotificationAsReadM(row.id);
}

const { mutate: markNotificationAsUnReadM } = useMutation(
  markNotificationAsUnread,
  {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['getNotifications'] });
      queryClient.invalidateQueries({ queryKey: ['getNotificationByQuery'] });
    },
  }
);
function markAsUnRead(row: Notification) {
  markNotificationAsUnReadM(row.id);
}

let announcement = ref(null)
axios
  .get('/api/v1/announcements')
  .then((res) => {
    announcement.value = res.data;
  });
</script>
