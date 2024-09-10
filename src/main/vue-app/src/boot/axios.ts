import { boot } from 'quasar/wrappers';
import axios, { AxiosInstance } from 'axios';
import { Notify } from 'quasar';
import Util from 'src/utils/util';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance;
  }
}

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create();

axios.interceptors.request.use(
  async function (config) {
    if (config.url.includes('/api/v1')) {
      config.url = process.env.QENV.PUBLIC_PATH + config.url;
    }
    if (
      !config.url.includes('/api/v1/auth') &&
      !config.url.includes('/api/v1/notifications') &&
      !config.url.includes('/api/v1/signRequest/newSignRequest') &&
      config.url.includes('/api/v1')
    ) {
      if (
        Util.methods.loggedIn() &&
        localStorage.tokenIsRefreshing === 'false'
      ) {
        localStorage.tokenIsRefreshing = true;
        const refresh = {
          refreshToken: localStorage.refreshToken,
        };
        await axios
          .post('/api/v1/auth/refresh', refresh, {
            headers: { 'Content-Type': 'application/json' },
          })
          .then((res) => {
            Util.methods.setLocalStorage(res.data);
          })
          .catch((error) => {
            console.log(error);
          })
          .finally(() => {
            localStorage.tokenIsRefreshing = false;
          });
      }
    }
    // Do something before request is sent
    config.headers['Accept-Language'] = localStorage.getItem('locale')
      ? localStorage.getItem('locale')
      : 'en';
    const token = localStorage.token;
    // add token to header
    if (token) {
      config.headers.Authorization = 'Bearer ' + token;
    }
    return config;
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error);
  }
);

// Add a response interceptor
axios.interceptors.response.use(
  function (response) {
    // Do something with response data
    return response;
  },
  function (error) {
    // Do something with response error
    console.log(error.response.config.url);
    console.log(error.response);
    if (error.response.status === 500) {
      Notify.create({
        message: 'Προέκυψε σφάλμα',
        color: 'negative',
      });
    } else if (error.response.status === 404) {
      Notify.create({
        message: JSON.stringify(error.response.data),
        color: 'warning',
      });
    } else if (error.response.status === 405) {
      Notify.create({
        message: JSON.stringify(error.response.data),
        color: 'error',
      });
    } else if (error.response.status === 403) {
      Notify.create({
        message: JSON.stringify(error.response.data),
        color: 'negative',
      });
    } else if (error.response.status === 401) {
      Notify.create({
        message: 'Δεν έχετε δικαιώματα για αυτή την ενέργεια',
        color: 'negative',
      });
    } else if (error.response.status === 400) {
      let message = error.response.data.parameterViolations
        ?.map((violation) => violation.message)
        .join('\n ');
      if (!message) {
        message = error.response.data;
      }
      Notify.create({
        message: JSON.stringify(message),
        color: 'warning',
      });
    } else if (error.response.status === 409) {
      Notify.create({
        message: JSON.stringify(error.response.data),
        color: 'negative',
      });
    }
    return Promise.reject(error);
  }
);

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export { axios, api };
