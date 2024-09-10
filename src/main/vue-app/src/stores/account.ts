import { defineStore } from 'pinia';

export const useAccountStore = defineStore('account', {
  state: () => ({
    userid: null,
    ivuser: null,
    email: null,
    fullName: null,
    depUnit: null,
    permissions: [],
  }),
  getters: {
    authenticated: (state) => state.email != null || state.fullName != null,
  },
  share: {
    // An array of fields that the plugin will ignore.
    //omit: ['foo'],
    // Override global config for this store.
    enable: true,
    initialize: true,
  },
});
