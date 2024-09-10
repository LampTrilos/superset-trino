<template>
  <div
    class="row q-my-md q-col-gutter-sm"
    v-if="userDepUnit == null && !allDepUnits"
  >
    <div class="col-12 col-md-6">
      <SelectDepartment
        v-model="selectedDepUnit"
        :label="t('department', 0)"
        type="generals"
        clearable
      ></SelectDepartment>
    </div>
  </div>
  <div
    class="row q-my-md q-col-gutter-sm"
    v-if="depUnit != null || allDepUnits"
  >
    <div class="col-12 col-md-4">
      <SelectUser v-model="user" :label="t('user', 0)" clearable hide-bottom-space></SelectUser>
    </div>
    <div class="col-12 col-md-4" v-if="showPartial">
      <SelectDepartment
        :key="depUnit"
        v-model="selectedParDepUnit"
        :label="t('department', 0)"
        type="partials"
        clearable
        :parent-dep-unit="depUnit"
        :manage="true"
      ></SelectDepartment>
    </div>
    <div class="col-12 col-md-4" v-if="showOu">
      <SelectOu v-model="selectedOu" :label="t('ou', 0)" clearable></SelectOu>
    </div>
    <div class="col-12 col-md-3">
      <q-btn
        color="secondary"
        icon="add"
        unelevated
        no-caps
        outline
        class="full-height"
        :disable="!user || (showPartial && selectedParDepUnit == null)"
        @click="add(user, role)"
      >
        {{ t('add') }}
      </q-btn>
    </div>
    <div class="col-12 col-md-12">
      <q-table
        :columns="columns"
        :visible-columns="visibleColumns"
        :rows="users"
        row-key="id"
        :rows-per-page-options="[-1]"
        :loading="usersIsFetching"
      >
        <template #body-cell-actions="propsBody">
          <q-td :props="propsBody">
            <q-btn
              round
              flat
              size="sm"
              icon="remove"
              @click="remove(propsBody.row)"
              ><q-tooltip>{{ t('remove') }}</q-tooltip></q-btn
            >
          </q-td>
        </template>
      </q-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import SelectUser from 'components/selects/SelectUser.vue';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import {
  getUsersByDepUnitAndRole,
  persistUserToDepUnitAndRole,
  removeUserRoleFromDepUnit,
} from 'src/services/users';
import { Loading, Notify } from 'quasar';
import SelectDepartment from 'components/selects/SelectDepartment.vue';
import { useCodeFormatter } from 'src/utils/code-formatter';
import SelectOu from 'components/selects/SelectOu.vue';
const { formatDep } = useCodeFormatter();

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    role: string;
    userDepUnit?: string | null;
    allDepUnits?: boolean;
    showPartial?: boolean;
    showOu?: boolean;
  }>(),
  {
    userDepUnit: () => null,
    allDepUnits: () => false,
    showPartial: () => false,
    showOu: () => false,
  }
);

let user = ref(null);
let selectedDepUnit = ref(null);
let selectedParDepUnit = ref(null);
let selectedOu = ref(null);

const columns = [
  {
    name: 'id',
    field: (row) => row.user.arithmosMitroou,
    required: true,
    label: t('mitroo'),
    align: 'left',
  },
  {
    name: 'lastName',
    field: (row) => row.user.lastName,
    required: true,
    label: t('lastName'),
    align: 'left',
  },
  {
    name: 'firstName',
    field: (row) => row.user.firstName,
    required: true,
    label: t('firstName'),
    align: 'left',
  },
  {
    name: 'department',
    required: false,
    label: t('department', 0),
    align: 'left',
    field: (row) =>
      row.depUnit != null
        ? '(' + formatDep(row.depUnit.code) + ') ' + (row.depUnit?.name ?? '')
        : '',
  },
  {
    name: 'ou',
    required: false,
    label: t('ou', 0),
    align: 'left',
    field: (row) => (row.ou != null ? row.ou.name : ''),
  },
  {
    name: 'actions',
  },
];

let depUnit = computed(() => {
  if (props.userDepUnit == null) {
    return selectedDepUnit.value == null ? null : selectedDepUnit.value.id;
  } else {
    return props.userDepUnit;
  }
});

let fetchQueryEnabled = computed(() => {
  if (props.allDepUnits) {
    return true;
  } else {
    return depUnit.value != null;
  }
});

const { data: users, isFetching: usersIsFetching } = useQuery({
  queryKey: ['getUsersByDepUnitAndRole', props.role, depUnit],
  queryFn: () =>
    getUsersByDepUnitAndRole({ depUnit: depUnit.value, role: props.role }),
  select: (data) => data.content,
  enabled: fetchQueryEnabled,
});

const queryClient = useQueryClient();

const { mutate: persistUserRole } = useMutation(persistUserToDepUnitAndRole, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({
      queryKey: ['getUsersByDepUnitAndRole', props.role, depUnit.value],
    });
    Notify.create({
      message: t('saveSuccess'),
    });
  },
  onSettled: () => {
    Loading.hide();
  },
});

function add(user, role) {
  persistUserRole({
    userId: user.id,
    depUnit: props.showPartial ? selectedParDepUnit.value.id : depUnit.value,
    ouId: props.showOu ? selectedOu.value.id : null,
    role: role,
  });
}

const { mutate: removeUserRole } = useMutation(removeUserRoleFromDepUnit, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({
      queryKey: ['getUsersByDepUnitAndRole', props.role, depUnit.value],
    });
    Notify.create({
      message: t('deleteSuccess'),
    });
  },
  onSettled: () => {
    Loading.hide();
  },
});

function remove(row) {
  removeUserRole({
    userId: row.user.id,
    depUnit: row.depUnit?.id,
    ouId: row.ou?.id,
    role: row.role,
  });
}

let visibleColumns = ref(['lastName', 'firstName', 'actions']);
if (props.showOu) {
  visibleColumns.value.push('ou');
}
if (!props.allDepUnits) {
  visibleColumns.value.push('department');
}
</script>
