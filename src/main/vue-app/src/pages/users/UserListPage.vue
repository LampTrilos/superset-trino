<template>
  <h5>{{ t('permissionBook', 1) }} - {{ t(role) }}</h5>

  <div v-if="role == 'genikos-diacheiristis-ylikou'">
    <UserRole
      :role="'GENIKOS_DIACHEIRISTIS_YLIKOU'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'tmimatarchis'">
    <UserRole
      :role="'TMIMATARCHIS'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'diefthyntis'">
    <UserRole
      :role="'DIEFTHYNTIS'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'anaplirotis-diefthyntis'">
    <UserRole
      :role="'ANAPLIROTIS_DIEFTHYNTIS'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'syntaktis-dapanon-promitheias'">
    <UserRole
      :role="'SYNTAKTIS_DAPANON_PROMITHEIAS'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'diacheiristis-kathikontoligou'">
    <UserRole
      :role="'DIACHEIRISTIS_KATHIKONTOLIGIOU'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'merikos-diacheiristis-ylikou'">
    <UserRole
      :role="'MERIKOS_DIACHEIRISTIS_YLIKOU'"
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
      :show-partial="true"
    />
  </div>
  <div v-if="role == 'topikos-ypeythynos'">
    <UserRoleTopikos
      :user-dep-unit="userManagesAllDepUnits ? null : depUnit"
    />
  </div>
  <div v-if="role == 'super-admin'">
    <UserRole :role="'SUPER_ADMIN'" :all-dep-units="true" />
  </div>
  <div v-if="role == 'diacheiristis-kodikologiou'">
    <UserRole :role="'DIACHEIRISTIS_KODIKOLOGIOU'" :all-dep-units="true" />
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import UserRole from 'components/users/UserRole.vue';
import { computed } from 'vue';
import { useAccountStore } from 'stores/account';
import { storeToRefs } from 'pinia';
import UserRoleTopikos from "components/users/UserRoleTopikos.vue";

const { t } = useI18n();

const accountStore = useAccountStore();
const { depUnit, permissions } = storeToRefs(accountStore);

const userManagesAllDepUnits = computed(() => {
  return permissions.value.some(
    (prm) =>
      prm.entity === 'DUTIES' &&
      ['VIEW', 'ALL'].includes(prm.action) &&
      prm.allDepUnits
  );
});

defineProps<{
  role: string;
}>();
</script>
