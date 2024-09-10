<template>
  <q-expansion-item
    v-if="children"
    :icon="icon"
    :label="title"
    v-model="expanded"
    :data-cy="'left-sidebar-' + title"
  >
    <SideBarLink
      class="q-pl-md"
      v-for="child in children.filter((lnk) => !!lnk.visible)"
      :key="child.title"
      v-bind="child"
    />
  </q-expansion-item>
  <q-item
    v-else
    clickable
    :to="link"
    class="drawer-item"
    active-class="side-bar-link-active"
    :data-cy="'left-sidebar-' + link.name"
    :href="href"
    :target="target"
  >
    <q-item-section v-if="icon" avatar>
      <q-btn flat square :icon="icon" class="side-bar-link">
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

    <q-item-section side top v-if="badge && !badge.rounded">
      <q-badge
        :color="badge.color"
        :label="badge.label"
        :rounded="badge.rounded"
        align="middle"
      />
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
import SideBarLink from 'components/sidebar/SideBarLink.vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';

export interface SideBarItemProps {
  title: string;
  caption?: string;
  link?: {
    name: string;
  };
  href?: string | null;
  icon?: string;
  badge?: {
    label: string;
    color: string;
    rounded: boolean;
  };
  children?: Array<SideBarItemProps>;
  target?: string;
}
const props = withDefaults(defineProps<SideBarItemProps>(), {
  caption: '',
  target: '_self',
});

const route = useRoute();
const expanded = ref(props.children?.some((e) => e.link?.name === route.name));
</script>
