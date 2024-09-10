<template>
  <div style="min-height: 21px">{{ isFetching ? 'Αναζήτηση πινακίδας στο πληροφοριακό σύστημα οχήματα' : '' }} </div>
  <div v-if="soapError">{{ t(soapError) }}</div>
  <div v-if="showDetails && ochimaSoap.id" class="row q-col-gutter-sm q-mb-md">
    <div v-if="ochimaSoap.id" class="q-mt-md">
      <div v-for="(value, key) in ochimaSoap" :key="key">
        <div class="row">
          <div class="col-5">
            {{ t(key) }}
          </div>
          <div class="col-1">:</div>
          <div class="col-6">
            {{ value }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup async lang="ts">
import { useI18n } from 'vue-i18n';
import {Ochima, SyndesiKinitou} from 'src/services/models';
import {computed, reactive, ref} from 'vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import InputDate from 'components/inputs/InputDate.vue';
import {getOchimaByRegNumSoap, getSyndesiKinitouById} from "src/services/items";
import {useQuery} from "@tanstack/vue-query";
import {useBreadcrumbsStore} from "stores/breadcrumbs";

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    modelValue?: string | null;
    arithmosKykloforiasOchimatos?: string | null;
    showDetails?: boolean;
  }>(),
  {
    showDetails: false

  }
);
const emit = defineEmits(['update:modelValue']);

let ochimaSoap = reactive({
  id: null,
  ac: null,
});

let soapError = ref(null);

const { isFetching, isError, error } = useQuery({
  queryKey: ['getOchimaByRegNumSoap', props.arithmosKykloforiasOchimatos],
  queryFn: () => getOchimaByRegNumSoap(props.arithmosKykloforiasOchimatos),
  enabled: props.arithmosKykloforiasOchimatos != null,
  onSettled: (res) => {
    soapError.value = null;
    if (res && res.id) {
      Object.assign(ochimaSoap, res);
      emit('update:modelValue', 'Επαληθεύτηκε')
    } else {
      emit('update:modelValue', 'Δεν επαληθεύτηκε')
      if (res && res.soapErrorCode != null) {
        if (res.soapErrorCode == '0' && !ochimaSoap.id) {
          soapError.value = 'Δεν βρέθηκε η πινακίδα'
        }
        if (res.soapErrorCode == '1') {
          soapError.value = res.soapErrorDescription
        }
      }
    }
  },
});
</script>
