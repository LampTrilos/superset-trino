<template>
  <h5>{{ t('settings') }}</h5>
  <div class="row q-col-gutter-sm">
    <div class="col-12 col-md-4">
      <q-form @submit="onSubmit" class="q-gutter-y-md">
        <q-input
          filled
          readonly
          v-model="user.email"
          :label="t('email')"
          class="required"
          :rules="requiredRule()"
        >
          <template #prepend>
            <q-icon name="mail" />
          </template>
        </q-input>
<!--        <q-input
          filled
          v-model="user.phone"
          :label="t('phone')"
          class="required"
          :rules="requiredRule()"
          mask="+30 ### #######"
          unmasked-value
        >
          <template #prepend>
            <q-icon name="smartphone" />
          </template>
        </q-input>-->

        <q-input
          filled
          readonly
          v-model="user.vathmosDescription"
          :label="t('vathmos')"
          class="required"
          :rules="requiredRule()"
        >
        </q-input>

<!--        <div>
          <q-btn
            :label="t('save')"
            type="submit"
            color="primary"
            no-caps
            unelevated
          />
        </div>-->
      </q-form>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n';
import { reactive } from 'vue';
import { useMutation, useQuery } from '@tanstack/vue-query';
import { Loading, Notify } from 'quasar';
import { getMyUser, postUser } from 'src/services/users';
import { useValidationRules } from 'src/utils/validation-rules';

const { t } = useI18n();

let user = reactive({
  email: null,
  phone: null,
});

useQuery({
  queryKey: ['getMyUser'],
  queryFn: () => getMyUser(),
  onSettled: (res) => {
    Object.assign(user, res);
  },
});

function onSubmit() {
  postUserM(user);
}

const { mutate: postUserM } = useMutation(postUser, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    Notify.create({
      message: t('updated-successfully'),
    });
  },
  onSettled: () => {
    Loading.hide();
  },
});

const { requiredRule } = useValidationRules();
</script>
