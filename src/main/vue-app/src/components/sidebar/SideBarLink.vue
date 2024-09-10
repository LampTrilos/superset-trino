<template>
  <q-item
    clickable
    :to="link"
    :href="href"
    :target="target"
    class="drawer-item"
    active-class="side-bar-link-active"
    :data-cy="'left-sidebar-' + link.name"
  >
    <q-item-section avatar>
      <q-btn flat square :icon="icon" style="color: inherit">
        <q-badge
          v-if="badge && badge.rounded"
          :color="badge.color"
          floating
          rounded
        />
      </q-btn>
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ title }}</q-item-label>
      <q-item-label caption>{{ caption }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
export interface SideBarLinkProps {
  title: string;
  caption?: string;
  link: {
    name: string;
  };
  icon?: string;
  badge?: {
    label: string;
    color: string;
    rounded: boolean;
  };
  href?: string | null;
  target?: string;
}
withDefaults(defineProps<SideBarLinkProps>(), {
  caption: '',
  target: '_self',
});
</script>

<style lang="sass">
.drawer-item
  border-radius: 0 24px 24px 0
  margin-right: 12px

.side-bar-link-active
  color: $primary
  background: white
</style>
