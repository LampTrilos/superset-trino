<template>
  <div class="row q-my-md q-col-gutter-sm">
    <div class="col-6 col-md-2">
      <q-input
        filled
        :label="t('serialNumber', 0)"
        clearable
        v-model.trim="search"
      >
      </q-input>
    </div>
    <div class="col-6 col-md-4">
      <SelectItemCode
        emit-value
        map-options
        v-model="itemCode"
      />
    </div>
    <div class="col-6 col-md-4">
      <q-select
        filled
        :label="t('status')"
        v-model.trim="metaptothike"
        :options="[
          {
            label: 'Απογράφηκε',
            value: 'true',
          },
          {
            label: 'Δεν Απογράφηκε',
            value: 'false',
          },
        ]"
      >
      </q-select>
    </div>
    <div class="col-12 col-md-3">
      <q-btn
        outline
        no-caps
        class="full-height"
        icon="search"
        @click="triggerSearch"
      >
        {{ t('search') }}
      </q-btn>
    </div>
  </div>
  <q-table
    flat
    bordered
    :columns="columns"
    :rows="data?.content"
    row-key="id"
    :rows-per-page-options="[10, 20, 50]"
    v-model:pagination="tablePagination"
    @request="onRequest"
    :loading="isFetching"
    :selection="multipleItems ? 'multiple' : 'single'"
    v-model:selected="selected"
  >
  </q-table>
  <div v-if="selected.length == 1" class="q-mt-md">
    <h6 class="q-my-sm">Επιλεγμένο</h6>
    <div v-for="(value, key) in selected[0]" :key="key">
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
  <div v-if="selected.length > 1" class="q-mt-md" >
    {{
      selected.length == 0
        ? 'Δεν έχετε επιλέξει υλικά'
        : 'Επιλεγμένα υλικά: ' +
        JSON.stringify(selected.map((sl) => sl.arithmosOploU), null, 1)
    }}
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useDateFormatter } from 'src/utils/date-formatter';
import {Item, PageResponse} from 'src/services/models';
import { computed, ref } from 'vue';
import {useQuery, useQueryClient} from '@tanstack/vue-query';
import { getMetaptosiGun } from 'src/services/items';
import { useCodeFormatter } from 'src/utils/code-formatter';
import SelectItemCode from "components/selects/SelectItemCode.vue";

const { formatDateTimeLocale } = useDateFormatter();
const { formatUser } = useCodeFormatter();
const { t } = useI18n();

let search = ref(null);
let metaptothike = ref(          {
  label: 'Δεν Απογράφηκε',
  value: 'false',
})
let itemCode = ref(null);
const columns = [
  {
    name: 'telikiYpiresia',
    required: true,
    label: t('telikiYpiresia'),
    align: 'left',
    field: 'telikiYpiresia',
  },
  {
    name: 'arithmosOploU',
    required: true,
    label: t('serialNumber'),
    align: 'left',
    field: 'arithmosOploU',
  },
  {
    name: 'kae',
    required: true,
    label: t('kae'),
    align: 'left',
    field: 'kae',
  },
  {
    name: 'metaptothike',
    label: t('metaptothike'),
    align: 'left',
    field: (row) => (row.metaptothike ? '✓' : ''),
  },
];

let tablePagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
});

let queryPagination = ref({
  size: tablePagination.value.rowsPerPage,
  index: tablePagination.value.page - 1,
});

const { data, isFetching } = useQuery({
  queryKey: ['getMetaptosiGun', queryPagination, search.value, itemCode.value],
  queryFn: () =>
    getMetaptosiGun(
      queryPagination.value.size,
      queryPagination.value.index,
      search.value,
      itemCode.value,
      metaptothike.value,
    ),
  onSuccess: (data: PageResponse<Item>) => {
    tablePagination.value.page = data.index + 1;
    tablePagination.value.rowsPerPage = data.size !== -1 ? data.size : 0;
    tablePagination.value.rowsNumber = data.total;
  },
  initialData: () => {
    return {
      content: [],
      contentSize: 0,
      total: 0,
      index: 0,
      size: 0,
    };
  },
});

function onRequest(props) {
  queryPagination.value.size = props.pagination?.rowsPerPage || -1;
  queryPagination.value.index = props.pagination?.page - 1;
}

const props = withDefaults(
  defineProps<{
    modelValue: any[] | undefined;
    multipleItems?: boolean;
  }>(),
  {
    modelValue: () => [],
    multipleItems: false,
  }
);

const emit = defineEmits(['update:modelValue']);
const selected = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

const queryClient = useQueryClient();
function triggerSearch() {
  queryClient.invalidateQueries({ queryKey: ['getMetaptosiGun'] });
}
</script>
