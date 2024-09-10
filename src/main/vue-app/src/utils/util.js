import { useAccountStore } from 'stores/account';

const accountStore = useAccountStore();
export default {
  methods: {
    parseJwt(token) {
      let base64Url = token.split('.')[1];
      let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      let jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
          })
          .join('')
      );
      return JSON.parse(jsonPayload);
    },
    clearLocalStorage() {
      let locale = localStorage.getItem('locale');
      localStorage.clear();
      sessionStorage.clear();
      localStorage.setItem('locale', locale);
    },
    isTokenExpired(token) {
      let decode = this.parseJwt(token);
      if (decode.exp * 1000 < new Date().getTime()) {
        return true;
      }
      return false;
    },
    setLocalStorage(res_data_obj) {
      let jwt = this.parseJwt(res_data_obj.access_token);
      localStorage.userfullname = jwt.family_name + ' ' + jwt.given_name;
      localStorage.userid = jwt.userid;
      localStorage.ivuser = jwt.ivuser;
      localStorage.token = res_data_obj.access_token;
      localStorage.refreshToken = res_data_obj.refresh_token;
      localStorage.expires_at = jwt.exp;
      let jwtRefresh = this.parseJwt(res_data_obj.refresh_token);
      localStorage.refresh_expires_at = jwtRefresh.exp;
      localStorage.tokenIsRefreshing = false;
      accountStore.$patch({
        fullName: localStorage.userfullname,
        userid: jwt.userid,
        ivuser: jwt.ivuser,
        email: jwt.email,
        depUnit: jwt.depunitid,
        authenticated: true,
      });
    },
    loggedIn() {
      if (localStorage.token && localStorage.refresh_expires_at) {
        let date_now = Date.now() / 1000;
        return localStorage.refresh_expires_at - date_now > 0;
      } else {
        return false;
      }
    },
    refreshTokenHasExpired() {
      if (localStorage.token && localStorage.expires_at) {
        let date_now = Date.now() / 1000;
        return localStorage.expires_at - date_now;
      } else {
        return true;
      }
    },
    tokenHasExpired() {
      if (localStorage.token && localStorage.expires_at) {
        let date_now = Date.now() / 1000;
        return localStorage.expires_at - date_now;
      } else {
        return true;
      }
    },
  },
};
