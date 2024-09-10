<template>
  <h5>{{ isNewUser ? t('add') : t('edit') }} {{ t('user', 2) }}</h5>
  <q-inner-loading v-if="isFetching" :showing="isFetching">
    <q-spinner-gears size="50px" color="primary" />
  </q-inner-loading>
  <div v-else-if="isError">Error: {{ error.message }}</div>
  <div v-else-if="user" style="max-width: 400px">
    <AlertUnsavedChanges
      v-if="!isNewUser"
      :object-to-watch="user"
      class="q-mb-md"
    />
    <q-form @submit="onSubmit">
      <q-input
        filled
        v-model="user.lastName"
        :label="t('lastName')"
        class="required"
        :rules="requiredRule()"
        clearable
      />
      <q-input
        filled
        v-model="user.firstName"
        :label="t('firstName')"
        class="required"
        :rules="requiredRule()"
        clearable
      />

      <q-select
        filled
        v-model="user.gender"
        label="Gender"
        class="required"
        :options="genders"
        :loading="gendersIsFetching"
        :option-value="(opt) => opt.id"
        :option-label="(opt) => opt.description"
        :rules="requiredRule()"
        clearable
      />

      <InputDate
        label="Birth Date From"
        v-model="user.birthDate"
        filled
        clearable
      />

      <div class="q-mt-lg">
        <q-btn
          :label="t('save')"
          type="submit"
          color="primary"
          no-caps
          unelevated
        />
        <q-btn
          class="q-ml-sm"
          :label="t('back')"
          outline
          no-caps
          :to="{ name: 'Users' }"
        />
      </div>
    </q-form>
  </div>
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getUser, postUser } from '../../services/users';
import { useBreadcrumbsStore } from 'stores/breadcrumbs';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import { getGenders } from 'src/services/genders';
import { Id } from 'src/services/types';
import { User } from 'src/services/models';
import { Loading, Notify } from 'quasar';
import InputDate from 'components/inputs/InputDate.vue';
import AlertUnsavedChanges from 'components/alerts/AlertUnsavedChanges.vue';
import { useValidationRules } from 'src/utils/validation-rules';

const { t } = useI18n();

const route = useRoute();
const id: Id = route.params.id;
const isNewUser = ref(id == null);

let user = reactive<User>({
  id: null,
  email: null,
  firstName: null,
  lastName: null,
  gender: null,
  birthDate: null,
});

const { isFetching, isError, error } = useQuery({
  queryKey: ['users', id],
  queryFn: () => getUser(id),
  enabled: !isNewUser.value,
  onSettled: (res) => {
    const breadcrumbsStore = useBreadcrumbsStore();
    breadcrumbsStore.setDynamicPropertyToLastBreadcrumb(
      res?.firstName + ' ' + res?.lastName
    );
    Object.assign(user, res);
  },
});

const queryClient = useQueryClient();
const router = useRouter();
const { mutate: postUserM } = useMutation(postUser, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['users'] });
    Notify.create({
      message: t('saveSuccess'),
    });
    router.push({ name: 'Users' });
  },
  onSettled: () => {
    Loading.hide();
  },
});

function onSubmit() {
  postUserM(user);
}

const { data: genders, isFetching: gendersIsFetching } = useQuery({
  queryKey: ['genders'],
  queryFn: () => getGenders(),
  select: (data) => data.content,
});

const { requiredRule } = useValidationRules();
</script>
