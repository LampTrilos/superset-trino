<template>
  <q-layout view="lHr LpR lfr">
    <TheHeader :count-down-formatted="countDownFormatted" :notifications="notifications" :full-name="fullName" :ivuser="ivuser"
               :email="email" :roles="roles" :departments="departments" :dep-unit="depUnit" :app-has-notifications="false"/>

    <q-drawer show-if-above v-model="leftDrawerOpen" side="left" dark style="border-right: 1px  white;">
      <q-space  />
      <div class="row  q-mb-md q-pb-sm q-pt-md bg-primary"  style="border-bottom: 3px solid white;">
        <q-space  class="bg-primary" />
        <!--        <q-icon size="32px" name="img:/icons/logo-white.svg" />-->
        <h5 class="q-my-sm  bg-primary" style="margin-left: -1rem;">Έλεγχος αεροδρομίων</h5>
        <q-space  class="bg-primary" />
      </div>
      <!--      <q-list>-->
      <!--        <SideBarItem-->
      <!--          :key="t('dashboard')"-->
      <!--          v-bind="{-->
      <!--            title: t('notification', 2),-->
      <!--            icon: 'notifications',-->
      <!--            link: {-->
      <!--              name: 'Dashboard',-->
      <!--            },-->
      <!--            visible: true,-->
      <!--          }"-->
      <!--          dark-->
      <!--        />-->
      <!--      </q-list>-->
      <q-list
        class="q-mb-md"
        v-if="leftSideBarLinks.filter((lnk) => !!lnk.visible).length > 0"
      >
        <!--        <q-item-label header class="q-px-md text-white"
          >Διαχείριση</q-item-label
        >-->
        <SideBarItem
          v-for="link in leftSideBarLinks.filter((lnk) => !!lnk.visible)"
          :key="link.title"
          v-bind="link"
          dark
        />
        <q-separator class="q-my-sm" />
      </q-list>
      <!--      <q-list>-->
      <!--        <q-item-label header class="q-px-md text-white"-->
      <!--          >Ο λογαριασμός μου</q-item-label-->
      <!--        >-->
      <!--        <SideBarItem-->
      <!--          v-for="link in myLeftSideBarLinks.filter((lnk) => !!lnk.visible)"-->
      <!--          :key="link.title"-->
      <!--          v-bind="link"-->
      <!--          dark-->
      <!--        />-->
      <!--      </q-list>-->
      <div class="row q-mt-lg q-mb-xs">
        <div class="q-ml-md">VERSION-DATE-DEPLOYED</div>
      </div>
    </q-drawer>
    <!-- No need for right drawer at the moment-->
    <!--    <q-drawer v-model="rightDrawerOpen" bordered side="right" :width="600">-->
    <!--      <div class="q-pa-md row items-center">-->
    <!--        <div class="col-1">-->
    <!--          <q-btn round unelevated icon="sym_o_close" @click="toggleRightDrawer">-->
    <!--            <q-tooltip>{{ t('close') }}</q-tooltip>-->
    <!--          </q-btn>-->
    <!--        </div>-->
    <!--        <div class="col-11">-->
    <!--          <div class="text-h5">{{ t('notification', 2) }}</div>-->
    <!--        </div>-->
    <!--      </div>-->
    <!--      <div>-->
    <!--        <q-list separator>-->
    <!--          <NotificationItem-->
    <!--            v-for="notification in notifications"-->
    <!--            :key="notification.id"-->
    <!--            v-bind="notification"-->
    <!--          />-->
    <!--        </q-list>-->
    <!--      </div>-->
    <!--    </q-drawer>-->

    <q-page-container >
      <!--      <q-page padding v-bind:style="elasBackground">-->
      <q-page padding >
        <BreadcrumbsList :breadcrumbs="breadcrumbs" />
        <router-view :key="$route.fullPath"/>
      </q-page>
    </q-page-container>

    <q-footer v-if="false">
      <q-toolbar>
        <q-toolbar-title>
          <div>Footer</div>
        </q-toolbar-title>
      </q-toolbar>
    </q-footer>
  </q-layout>
</template>

<script setup lang="ts" async>
import { useNotificationStore } from '../stores/notifications';
import NotificationItem from 'components/notifications/NotificationItem.vue';

import { storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
import Util from 'src/utils/util';
import { useBreadcrumbsStore } from 'stores/breadcrumbs';
import { computed, onBeforeUnmount, ref, provide } from 'vue';
import BreadcrumbsList from 'components/breadcrumbs/BreadcrumbsList.vue';
import SideBarItem from 'components/sidebar/SideBarItem.vue';
import { useQuery } from '@tanstack/vue-query';
import { getNotifications } from 'src/services/notification';
import { getMyDepartments, getMyPermissions } from 'src/services/permissions';
import { useRouter } from 'vue-router';
import axios from 'axios';
import TheHeader from "layouts/TheHeader.vue";

import { useAccountStore } from '../stores/account';
const accountStore = useAccountStore();
const { userid, email, fullName, depUnit, permissions, ivuser } =
  storeToRefs(accountStore);
//Background Image
const elasBackground = computed(() => {
  return  {
    backgroundΙmage: `url(${require('../assets/logoElasLessBlur.svg')})`
  }
})


// permissions
// getMyPermissions();

//Dont know what this is for
// const myLeftSideBarLinks = computed(() => {
//   return [];
// });

const leftSideBarLinks = computed(() => {
  return [
    {
      title: t('File Upload'),
      icon: 'cloud_upload',
      link: {
        name: 'Dashboard',
      },
      visible: true,
    },
    // {
    //   title: t('Application_Templates'),
    //   icon: 'description',
    //   link: {
    //     name: 'Account',
    //   },
    //   visible: true,
    // },
    {
      title: t('Superset'),
      icon: 'bar_chart',
      link: {
        name: 'Superset',
      },
      visible: true,
    },
    // {
    //   title: t('problemReport'),
    //   icon: 'sym_o_report',
    //   link: {
    //     name: 'ProblemReport',
    //   },
    //   visible: true,
    // },
    // {
    //   title: t('manual'),
    //   icon: 'sym_o_book',
    //   link: {
    //     name: 'Manual',
    //   },
    //   href: 'http://support.ydt/Training/Manual_ERPv1.0.pdf',
    //   target: '_blank',
    //   visible: true,
    // },

    // {
    //   title: t('my-transactions'),
    //   icon: 'mdi-account-arrow-left-outline',
    //   children: [
    //     {
    //       title: t('transaction-completed'),
    //       link: {
    //         name: 'MyCompoundTransactions',
    //       },
    //       visible: true,
    //     },
    //     {
    //       title: t('itransaction-completed'),
    //       link: {
    //         name: 'MyITransactions',
    //       },
    //       visible: true,
    //     },
    //     {
    //       title: t('transaction-pending', 1),
    //       link: {
    //         name: 'MyCompoundTransactionsPending',
    //       },
    //       visible: true,
    //     },
    //     {
    //       title: t('transaction-for-sign'),
    //       link: {
    //         name: 'MyCompoundTransactionsForSign',
    //       },
    //       visible: true,
    //     },
    //     /*        {
    //       title: t('transaction-draft', 1),
    //       link: {
    //         name: 'MyCompoundTransactionsDraft',
    //       },
    //       visible: true,
    //     },*/
    //   ],
    //   visible: true,
    // },
  ];
});

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
provide("toggleLeftDrawer", toggleLeftDrawer)

const rightDrawerOpen = ref(false);

function toggleRightDrawer() {
  rightDrawerOpen.value = !rightDrawerOpen.value;
}
provide("toggleRightDrawer", toggleRightDrawer)

let loggingOut = ref(false);
//No need for notifications in this Seal app
let enabledNotifications = ref(false);
// const notificationStore = useNotificationStore();
// const { notifications } = storeToRefs(notificationStore);
// const notificationsPageSize = 25;
// useQuery({
//   queryKey: ['getNotifications', notificationsPageSize, 0, null, email],
//   queryFn: () => getNotifications(notificationsPageSize, 0, null),
//   refetchInterval: 10000,
//   onSettled: (res) => {
//     notificationStore.$patch({
//       notifications: res.content,
//     });
//   },
//   enabled: enabledNotifications,
// });

const router = useRouter();
function logout() {
  enabledNotifications.value = false;
  loggingOut.value = true;
  router.push({
    name: 'LogOut',
  });
}
provide("logout", logout)

// breadcrumbs
const { breadcrumbs } = storeToRefs(useBreadcrumbsStore());

// departments
// const { data: departments } = useQuery({
//   queryKey: ['getMyDepartments'],
//   queryFn: () => getMyDepartments(),
// });
//
// function changeDepartment(val: any) {
//   console.log('Change department id: ' + val);
//   enabledNotifications.value = false;
//   loggingOut.value = true;
//   router.push({
//     name: 'AutoLogin',
//     query: { depunitid: val },
//   });
// }
// provide("changeDepartment", changeDepartment)

//timer
let dashboardUnmounted = false;
let countDown = ref(null);
function countDownTimer() {
  if (dashboardUnmounted) {
    return;
  }
  if (localStorage.tokenIsRefreshing === 'true' || loggingOut.value) {
    setTimeout(() => {
      countDownTimer();
    }, 1000);
  }
  let date_now = Date.now() / 1000;
  let temp = localStorage.expires_at - date_now;
  if (temp > 0) {
    setTimeout(() => {
      temp -= 1;
      countDown.value = temp;
      countDownTimer();
    }, 1000);
  } else {
    console.log('Refresh authToken has expired - go to logout');
    logout();
  }
}

const countDownFormatted = computed(() => {
  if (countDown.value == null) {
    return '...';
  }
  return new Date(countDown.value * 1000).toISOString().slice(11, 19);
});

countDownTimer();

function refreshToken() {
  localStorage.tokenIsRefreshing = true;
  const refresh = {
    refreshToken: localStorage.refreshToken,
  };
  axios
    .post('/api/v1/auth/refresh', refresh, {
      headers: { 'Content-Type': 'application/json' },
    })
    .then((res) => {
      console.log('Token refreshed');
      Util.methods.setLocalStorage(res.data);
    })
    .catch((error) => {
      console.log(error);
    })
    .finally(() => {
      localStorage.tokenIsRefreshing = false;
    });
}
provide("refreshToken", refreshToken)

onBeforeUnmount(() => {
  // Stop the execution of the function by setting the ref to null
  dashboardUnmounted = true;
});

// let roles = ref('');
// axios
//   .get('/api/v1/users/my-roles')
//   .then((res) => {
//       roles.value = res.data;
//   })
//   .catch((error) => {
//       console.log(error);
//   })
</script>
