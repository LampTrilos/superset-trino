<template>
  <q-item :class="{ 'light-dimmed-2': read }" rounded="rounded">
    <q-item-section>
      <q-item-label v-if="type == 'TRANSACTION_SIGN'"
        >{{ 'Εκκρεμεί η υπογραφή της συναλλαγής ' }}
        <router-link
          class="text-primary"
          :to="{
            name: attributes.myCompoundTransaction
              ? 'MyCompoundTransactionSign'
              : 'CompoundTransactionSign',
            params: {
              id: attributes.compTransactionId,
            },
            query: { type: attributes.compTransactionType },
          }"
          >{{ attributes.compTransactionUdc }}</router-link
        ></q-item-label
      >
      <q-item-label v-else-if="type == 'TRANSACTION_COMPLETED'"
      >{{ 'Η συναλλαγή ολοκληρώθηκε ' }}
        <router-link
          class="text-primary"
          :to="{
            name: attributes.myCompoundTransaction
              ? 'MyCompoundTransactionSign'
              : 'CompoundTransactionSign',
            params: {
              id: attributes.compTransactionId,
            },
            query: { type: attributes.compTransactionType },
          }"
        >{{ attributes.compTransactionUdc }}</router-link
        ></q-item-label
      >
      <q-item-label v-else>{{ message }}</q-item-label>
      <q-item-label>{{ message }} {{ attributes?.compTransactionType ? ' ' + t('transaction-' + attributes.compTransactionType.toLowerCase()) : '' }}</q-item-label>
      <q-item-label caption>{{ timeAgo(createdTimestamp) }}</q-item-label>
    </q-item-section>

    <q-item-section side top>
      <q-btn
        v-if="read"
        color="primary"
        icon="notifications_on"
        size="sm"
        round
        flat
        @click="markAsUnRead"
      />
      <q-btn
        v-if="!read"
        color="primary"
        icon="notifications_off"
        size="sm"
        round
        flat
        @click="markAsRead"
      />
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
import { useDateFormatter } from 'src/utils/date-formatter';
import { useMutation, useQueryClient } from '@tanstack/vue-query';
import {
  markNotificationAsRead,
  markNotificationAsUnread,
} from 'src/services/notification';
import {useI18n} from "vue-i18n";

const { t } = useI18n();
const { timeAgo } = useDateFormatter();
export interface NotificationProps {
  id: number;
  message: string;
  type: string;
  createdTimestamp: string;
  read: boolean;
  attributes: { [key: string]: any };
}

const props = withDefaults(defineProps<NotificationProps>(), {});

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

function markAsRead() {
  markNotificationAsReadM(props.id);
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
function markAsUnRead() {
  markNotificationAsUnReadM(props.id);
}
</script>
