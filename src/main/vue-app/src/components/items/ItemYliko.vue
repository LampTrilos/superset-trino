<template>
  <component
    :is="is"
    v-model="copiedItem"
    :editable="editable"
    :multiple-items="multipleItems"
    :metaptosi="metaptosi"
    :restrictions="restrictions"
    :kae="kae"
    :itemCodeId="itemCodeId"
    :depUnitCode="depUnitCode"
    @onMetaptosiMultiple="onMetaptosiMultipleHandler"
  />
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import { Item } from 'src/services/models';
import {computed, defineAsyncComponent} from 'vue';
import {Id} from "src/services/types";
import axios from "axios/index";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue: Item | null;
    editable: boolean;
    multipleItems?: boolean;
    metaptosi?: boolean;
    restrictions?: boolean;
    template: string;
    kae?: string;
    itemCodeId?: Id;
    depUnitCode?: string;
  }>(),
  {
    modelValue: () => null,
    editable: true,
    multipleItems: false,
  }
);

const emit = defineEmits(['update:modelValue', 'onMetaptosiMultiple']);
const copiedItem: Item = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

const templateComponentMap = {
  base: () => import('components/items/ItemBase.vue'),
  gun: () => import('components/items/ItemGun.vue'),
  avl: () => import('components/items/ItemAvl.vue'),
  avlpda: () => import('components/items/ItemAvlpda.vue'),
  keraia: () => import('components/items/ItemKeraia.vue'),
  kinito: () => import('components/items/ItemKinito.vue'),
  parelkomenopompodekti: () => import('components/items/ItemParelkomenopompodekti.vue'),
  aleksisfairo: () => import('components/items/ItemAleksisfairo.vue'),
  antivalistikiaspida: () => import('components/items/ItemAntivalistikiaspida.vue'),
  antivalistikiplaka: () => import('components/items/ItemAntivalistikiplaka.vue'),
  antivalistikokranos: () => import('components/items/ItemAntivalistikokranos.vue'),
  pompodektisanalogikos: () => import('components/items/ItemPompodektisanalogikos.vue'),
  pompodektispsifiakos: () => import('components/items/ItemPompodektispsifiakos.vue'),
  pompodektisanalogikospsifiakos: () => import('components/items/ItemPompodektisanalogikospsifiakos.vue'),
  texnikamesa: () => import('components/items/ItemTexnikamesa.vue'),
  mpatariapompodekti: () => import('components/items/ItemMpatariapompodekti.vue'),
  ochima: () => import('components/items/ItemOchima.vue'),
  assetpliroforiki: () => import('components/items/ItemAssetpliroforiki.vue'),
  ypodima: () => import('components/items/ItemYpodima.vue'),
  roucho: () => import('components/items/ItemRoucho.vue'),
  panteloni: () => import('components/items/ItemPanteloni.vue'),
};

const is = computed(
  () => {
    const template = props.template.toLowerCase();
    console.log(template)
    if (template && template in templateComponentMap) {
      return defineAsyncComponent(templateComponentMap[template]);
    } else {
      return null; // Default case if template is not found or not handled
    }
  }
)

function onMetaptosiMultipleHandler(selected) {
  emit('onMetaptosiMultiple', selected)
}
</script>
