<template>
  <q-header class="bg-primary text-white q-header--bordered">
    <q-toolbar>
      <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" />

      <q-space />

      <div class="q-pa-md q-gutter-sm">
        <q-btn square flat padding="xs" icon="search" v-if="false">
          <q-tooltip>{{ t('search') }}</q-tooltip>
        </q-btn>

        <q-btn outline padding="sm" @click="refreshToken">
          <q-icon left size="xs" name="refresh" />
          <div>{{ countDownFormatted }}</div>
          <q-tooltip>{{ t('refresh-timer') }}</q-tooltip>
        </q-btn>

        <q-btn
          v-if="appHasNotifications"
          square
          flat
          padding="xs"
          icon="notifications"
          @click="toggleRightDrawer"
        >
          <q-badge
            v-if="notifications.filter((nt) => !nt.read).length"
            floating
            color="negative"
            rounded
          />
          <q-tooltip>{{ t('notification', 2) }}</q-tooltip>
        </q-btn>

        <q-btn square flat>
          {{ fullName }}
          <q-icon size="xs" name="expand_more" />
          <q-tooltip>{{ t('myaccount') }}</q-tooltip>
          <q-menu style="min-width: 250px">
            <div class="row q-pa-md">
              <div class="col-12 col-md-12">
                <div class="text-subtitle2 q-mt-md q-mb-xs">
                  {{ '(' + ivuser + ') ' + fullName }}
                </div>
              </div>
              <div class="col-12 col-md-12">
                <div class="text-subtitle2 q-mb-xs">{{ email }}</div>
              </div>
              <div class="col-12 col-md-12">
                <div class="q-mb-xs">{{ roles }}</div>
              </div>
              <div
                class="col-12 col-md-12"
                v-if="departments != null && departments.length > 0"
              >
                <div class="text-subtitle2 q-mt-md q-mb-xs">
                  <q-select
                    filled
                    :model-value="depUnit"
                    :options="departments"
                    :option-value="(opt) => opt.id"
                    :option-label="(opt) => opt.name"
                    emit-value
                    map-options
                    @update:model-value="changeDepartment"
                  />
                </div>
              </div>
              <div class="col-12 col-md-12 q-mt-md">
                <q-btn
                  dense
                  flat
                  icon="logout"
                  :label="t('logout')"
                  v-close-popup
                  @click="logout"
                />
              </div>
            </div>
          </q-menu>
        </q-btn>
      </div>
    </q-toolbar>
  </q-header>
</template>
<script setup lang="ts">
import { inject } from "vue";
import { useI18n } from 'vue-i18n';
import vue from "@vitejs/plugin-vue";
const { t } = useI18n();
//Are provided by DashboardLayout.vue.vue
const refreshToken = inject<() => void>('refreshToken');
const logout = inject<() => void>('logout');
const toggleRightDrawer = inject<() => void>('toggleRightDrawer');
const toggleLeftDrawer = inject<() => void>('toggleLeftDrawer');
const changeDepartment = inject<() => void>('changeDepartment');
//Props
const props = withDefaults(
  defineProps<{
    countDownFormatted: bigint;
    notifications: any;
    fullName: string;
    ivuser: string;
    email: string;
    roles: any;
    departments: any;
    depUnit: any;
    appHasNotifications: boolean;
  }>(),
   {
     appHasNotifications: false
   }
 );
</script>
