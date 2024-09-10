import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  /*  {
    path: '/',
    component: () => import('layouts/LandingLayout.vue'),
    children: [
      {
        name: 'Home',
        path: '/',
        component: () => import('pages/landing/HomePage.vue'),
        meta: {
          title: 'Home',
          requiresAuth: false,
        },
      },
    ],
  },*/
  {
    path: '/app',
    component: () => import('layouts/DashboardLayout.vue'),
    children: [
      {
        name: 'ManageApplications',
        path: 'manage_applications',
        component: () => import('pages/applications/ManageApplications.vue'),
        meta: {
          title: 'AllowedApplications',
          requiresAuth: true,
        },
      },
        //children: [
          {
            name: 'ApplicationAddEdit',
            path: 'application_add_edit/:id',
            component: () => import('pages/applications/ApplicationAddEdit.vue'),
            meta: {
              title: 'EditApplication',
              requiresAuth: true,
              breadcrumbs: [
                {
                  label: { key: 'Allowed_Applications', content: 1 },
                  to: {
                    name: 'ManageApplications',
                  },
                },
                {
                  label: { key: 'New_Application', content: 0 },
                },
              ],
            },
          },
          //]
      //},
      //-----------------------------ERP Routes below-----------------------------//
      {
        name: 'Dashboard',
        path: 'dashboard',
        component: () => import('pages/dashboard/DashboardPage.vue'),
        meta: {
          title: 'Dashboard',
          requiresAuth: true,
        },
      }
        ],
      },
  {
    name: 'AutoLogin',
    path: '/autologin',
    component: () => import('pages/auth/LoginAuto.vue'),
    alias: '/',
  },
  {
    name: 'LogOut',
    path: '/logout',
    component: () => import('pages/auth/LogOut.vue'),
  },
  //For OTP page serving, the page receives as params the userId, the templateCode, and the rowId, so it knows what to attempt to sign
  //Also receives a callback url, in order to notify the original app of the outcome of the otp validation
  {
    name: 'otp',
    //path: '/otp/:userId/:templateCode/:rowId',
    //example localhost:8081/otp?userId=275330&templateCode=vas-122&rowId=11223344&callbackUrl=https://www.google.com/search?q=rowId
    path: '/otp',
    component: () => import('pages/Transactions/Otp-dialog.vue'),
  },
  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/errors/ErrorNotFoundPage.vue'),
  },
];

export default routes;
