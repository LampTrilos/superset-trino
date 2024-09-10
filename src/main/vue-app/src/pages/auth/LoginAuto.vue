<template>
  <q-layout view="lHh Lpr lFf">
    <q-page-container>
      <q-page class="flex flex-center bg-grey-2">
        <div v-if="!error">Σύνδεση...</div>
        <q-card v-else class="q-pa-md shadow-2 my_card" bordered>
          <q-card-section class="text-center">
            <div class="text-grey-9 text-h5 text-weight-bold">Log in</div>
          </q-card-section>
          <q-card-section>
            <q-input
              dense
              outlined
              v-model="loginRequest.username"
              label="Username/User Id"
              @keydown.enter="login"
            ></q-input>
            <q-input
              dense
              outlined
              class="q-mt-md"
              v-model="loginRequest.password"
              type="password"
              label="Password"
              @keydown.enter="login"
            ></q-input>
          </q-card-section>
          <q-card-section>
            <q-btn
              style="border-radius: 8px"
              color="dark"
              rounded
              size="md"
              label="Log in"
              no-caps
              class="full-width"
              @click="login"
            ></q-btn>
          </q-card-section>
        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>
<script setup lang="ts">
import { useAccountStore } from '../../stores/account';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import util from 'src/utils/util';
import { useQueryClient } from '@tanstack/vue-query';
import { reactive, ref } from 'vue';

const accountStore = useAccountStore();
const route = useRoute();
const router = useRouter();

const queryClient = useQueryClient();
queryClient.clear();
localStorage.clear();
sessionStorage.clear();
accountStore.$reset();

let error = ref(false);
axios
  .post(
    '/api/v1/auth/autologin' +
      (route.query.depunitid ? '/' + route.query.depunitid : '')
  )
  .then((res) => {
    console.log('Auto Login success');
    util.methods.setLocalStorage(res.data);
    router.push({
      name: 'ManageApplications',
    });
  })
  .catch((err) => {
    console.log(err);
    console.log('Error auto login');
    error.value = true;
  });

//manual login
let loginRequest = reactive({ username: null, password: null });
function login() {
  axios
    .post('/api/v1/auth/login', loginRequest)
    .then((res) => {
      console.log('Manual Login success');
      util.methods.setLocalStorage(res.data);
      router.push({
        name: 'Dashboard',
      });
    })
    .catch((error) => {
      console.log(error);
      console.log('Error auto login');
      error.value = true;
    });
}
</script>
