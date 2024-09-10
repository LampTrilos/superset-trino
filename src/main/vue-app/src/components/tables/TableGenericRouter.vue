<template>
  <q-table
    :wrap-cells="true"
    :columns="columns"
    :rows="pageResponse?.content"
    :row-key="rowKey"
    v-model:pagination="pagination"
    @request="onRequest"
    :loading="isFetching"
    :rows-per-page-options="rowsPerPageOptions"
    :visible-columns="visibleColumns"
    v-bind="$attrs"
  >
    <template #top>
      <q-space />
      <q-btn
        v-if="!hideExportButton"
        class="q-mr-md"
        flat
        outline
        color="primary"
        icon="sym_o_download"
        label="Αρχείο"
        no-caps
        @click="exportTable"
        :loading="loadingExport"
      />
      <q-select
        v-model="visibleColumns"
        multiple
        outlined
        dense
        options-dense
        :display-value="$q.lang.table.columns"
        emit-value
        map-options
        :options="columns"
        option-value="name"
        style="min-width: 150px"
      />
    </template>
    <template #header="propsHeader">
      <q-tr :props="propsHeader">
        <q-th auto-width v-if="expanded" />
        <q-th
          v-for="col in propsHeader.cols.filter((cl) =>
            visibleColumns.includes(cl.name)
          )"
          :key="col.name"
          :props="propsHeader"
        >
          <div
            v-if="col.serverSideSorting"
            @click="sortColumn(col)"
            class="cursor-pointer"
          >
            {{ col.label }}
            <q-icon
              v-if="col.serverSideSortingOrder === 'asc'"
              name="arrow_upward"
            />
            <q-icon
              v-if="col.serverSideSortingOrder === 'desc'"
              name="arrow_downward"
            />
            <q-icon name="sort" />
            {{ col.serverSideSortingPriority ?? '' }}
          </div>
          <div v-else>
            {{ col.name !== 'actions' ? col.label : '' }}
          </div>
        </q-th>
      </q-tr>
    </template>
    <template #body="propsBody">
      <q-tr :props="propsBody">
        <q-td auto-width v-if="expanded">
          <q-toggle
            v-model="propsBody.expand"
            checked-icon="add"
            unchecked-icon="remove"
          />
        </q-td>
        <q-td
          v-for="col in propsBody.cols
            .filter((cl) => visibleColumns.includes(cl.name))
            .filter((obj) => obj.name !== 'actions')"
          :key="col.name"
          :props="propsBody"
        >
          <router-link
            class="text-primary"
            v-if="col.value != null && col.link && col.link(propsBody.row)"
            :to="col.link(propsBody.row)"
            >{{ col.value }}</router-link
          >
          <span
            class="text-primary cursor-pointer"
            v-else-if="
              col.value != null && col.action && col.action(propsBody.row)
            "
            @click="onActionClick(col.action(propsBody.row), propsBody.row)"
            >{{ col.value }}</span
          >
          <span v-else>{{ col.value }}</span>
        </q-td>
        <q-td
          v-if="propsBody.cols.some((obj) => obj.name === 'actions')"
          :props="propsBody"
          :key="'actions'"
        >
          <q-btn
            v-for="action in actions?.filter(filterActions(propsBody.row))"
            :key="'action_' + action.text"
            :round="action.icon != null"
            flat
            :size="action.icon == null ? 'md' : 'sm'"
            :icon="action.icon"
            color="primary"
            no-caps
            @click="onActionClick(action, propsBody.row)"
            :data-cy="action.method"
            ><q-tooltip v-if="action.icon">{{ action.text }}</q-tooltip
            >{{ action.icon == null ? action.text : '' }}</q-btn
          >
          <q-btn
            v-if="editRouter && !readOnly"
            round
            flat
            size="sm"
            icon="sym_o_edit"
            color="primary"
            :to="{ name: editRouter, params: { id: propsBody.row.id } }"
            ><q-tooltip>{{ t('edit') }}</q-tooltip></q-btn
          >
          <q-btn
            v-if="showDeleteAction && !readOnly"
            round
            flat
            size="sm"
            icon="mdi-delete-outline"
            color="primary"
            @click="openDialog(onDelete, propsBody.row, 'delete')"
            ><q-tooltip>{{ t('delete') }}</q-tooltip></q-btn
          >
        </q-td>
      </q-tr>
      <q-tr v-if="propsBody.expand" :props="propsBody" class="bg-grey-2">
        <q-td colspan="100%">
          <slot name="expandedSlot" :row="propsBody.row"></slot>
        </q-td>
      </q-tr>
    </template>
  </q-table>
  <q-dialog v-model="confirmDialog.open" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">{{ t('doYouConfirmThisAction') }}</div>
      </q-card-section>
      <q-card-section class="column items-center">
        <div class="q-mx-auto">
          {{ deleteKey.text }}: {{ deleteKey.value(confirmDialog.item) }}
        </div>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn :label="t('cancel')" outline v-close-popup no-caps />
        <q-btn
          :label="t(confirmDialog.actionName)"
          color="primary"
          @click="confirmDialog.action(confirmDialog.item)"
          v-close-popup
          no-caps
          unelevated
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <span v-if="isError">{{ t('error') }}: {{ error.message }}</span>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
import { useRoute, useRouter } from 'vue-router';
import { PageResponse } from 'src/services/models';
import { exportFile, Loading, Notify, useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import axios from 'axios';

const { t } = useI18n();

const props = defineProps({
  columns: {
    type: Array,
    required: true,
  },
  rowKey: {
    type: [String, Function],
    required: false,
    default: 'id',
  },
  rowsPerPageOptions: {
    type: Array,
    required: false,
    default: () => [10, 25, 50, 100],
  },
  queryKey: {
    type: String,
    required: true,
  },
  getRowsFunction: {
    type: Function,
    required: true,
  },
  editRouter: {
    type: String,
    required: false,
    default: null,
  },
  showDeleteAction: {
    type: Boolean,
    required: false,
    default: true,
  },
  deleteRowFunction: {
    type: Function,
    required: false,
    default: () => Promise.resolve(),
  },
  deleteKey: {
    type: Object,
    required: false,
    default: () => ({ text: 'Id', value: 'id' }),
  },
  expanded: {
    type: Boolean,
    required: false,
    default: false,
  },
  actions: {
    type: Array,
    required: false,
    default: () => null,
  },
  readOnly: {
    type: Boolean,
    required: false,
    default: false,
  },
  hideExportButton: {
    type: Boolean,
    required: false,
    default: false,
  },
  serverExportUrl: {
    type: String,
    required: false,
    default: '',
  },
});

const route = useRoute();

if (route.query.sort) {
  const sortList = route.query.sort.split(',');
  for (let i = 0; i < sortList.length; i++) {
    let sortTuple = sortList[i].split(' ');
    let result = props.columns.find((item) => item.name === sortTuple[0]);
    if (result) {
      result.serverSideSortingOrder = sortTuple[1];
      result.serverSideSortingPriority = i + 1;
    }
  }
}

let pagination = ref({});
const {
  isFetching,
  isError,
  data: pageResponse,
  error,
} = useQuery({
  queryKey: [props.queryKey],
  queryFn: () =>
    props.getRowsFunction(new URLSearchParams(route.query).toString()),
  onSuccess: (data: PageResponse) => {
    if (pageExceedsLastPage(data)) {
      const calculateLastPage =
        data.total < data.size ? 1 : Math.ceil(data.total / data.size);
      const query = {
        ...route.query,
        index: calculateLastPage - 1,
      };
      requestRouter(query);
    } else {
      pagination.value.page = data.index + 1;
      pagination.value.rowsPerPage = data.size !== -1 ? data.size : 0;
      pagination.value.rowsNumber = data.total;
    }
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

const router = useRouter();

function onRequest(props) {
  console.debug(props.pagination);

  const query = {
    ...route.query,
    index: props.pagination?.page - 1,
    size: props.pagination?.rowsPerPage || -1,
  };

  requestRouter(query);
}

function pageExceedsLastPage(data: PageResponse) {
  return data.contentSize === 0 && data.index !== 0;
}

function sortColumn(column) {
  let querySort = '';

  // Check if a sort query parameter exists
  if (route.query.sort) {
    const currentSort = route.query.sort;

    // If the current sort parameter already includes the selected column and ascending order, switch to descending order
    if (currentSort.includes(column.name + ' asc')) {
      querySort = currentSort.replace(
        column.name + ' asc',
        column.name + ' desc'
      );
    }
    // If the current sort parameter already includes the selected column and descending order, remove it from the sort parameter
    else if (currentSort.includes(column.name + ' desc')) {
      // Use regular expressions to remove leading/trailing commas and double commas from the querySort parameter when removing a column from it
      querySort = currentSort
        .replace(column.name + ' desc', '')
        .replace(',,', ',')
        .replace(/(^,)|(,$)/g, '');
    }
    // If the current sort parameter does not include the selected column, add it to the parameter with ascending order
    else {
      querySort = currentSort + ',' + column.name + ' asc';
    }
  }
  // If no sort query parameter exists, create a new one with the selected column and ascending order
  else {
    querySort = column.name + ' asc';
  }

  const query = {
    ...route.query,
    sort: querySort || undefined, // remove sort parameter if it is empty
  };

  requestRouter(query);
}

const confirmDialog = ref({
  open: false,
  action: null,
  item: {},
  actionName: 'Submit',
});

function openDialog(action, row, actionName: string) {
  console.log('[Open dialog] Item Id: ' + row.id);
  confirmDialog.value.open = true;
  confirmDialog.value.item = row;
  confirmDialog.value.action = action;
  confirmDialog.value.actionName = actionName;
}

const queryClient = useQueryClient();

function onDelete(item) {
  console.log('[Delete] Item Id: ' + item.id);
  deleteItemM(item.id);
}

const { mutate: deleteItemM } = useMutation(props.deleteRowFunction, {
  onMutate: () => {
    Loading.show();
  },
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: [props.queryKey] });
    Notify.create({
      message: t('deleteSuccess'),
    });
  },
  onSettled: () => {
    Loading.hide();
  },
});

const emits = defineEmits([
  'view',
  'sign',
  'markAsRead',
  'markAsUnRead',
  'reject',
  'viewBalance',
  'viewTotalBalance',
  'viewUserBalance',
  'viewAssignedBalance',
]);
function onActionClick(action, item) {
  console.log('[Table] Click action ' + action.method);
  emits(action.method, item);
}

function requestRouter(query) {
  router.push({ query });
}

let visibleColumns = ref(props.columns?.map((cl) => cl.name));

function filterActions(row) {
  return function (action) {
    if (action.attribute) {
      if (Array.isArray(action.attribute.value)) {
        return action.attribute.value.includes(action.attribute.column(row));
      } else {
        return action.attribute.column(row) == action.attribute.value;
      }
    } else {
      return true;
    }
  };
}

function wrapCsvValue(val, formatFn, row) {
  let formatted = formatFn !== void 0 ? formatFn(val, row) : val;

  formatted =
    formatted === void 0 || formatted === null ? '' : String(formatted);

  formatted = formatted.split('"').join('""');
  /**
   * Excel accepts \n and \r in strings, but some other CSV parsers do not
   * Uncomment the next two lines to escape new lines
   */
  // .split('\n').join('\\n')
  // .split('\r').join('\\r')

  return `"${formatted}"`;
}
const $q = useQuasar();

const loadingExport = ref(false);

async function exportTable() {
  let q = new URLSearchParams(route.query).toString();
  loadingExport.value = true;
  const response = await axios.get(props.serverExportUrl + q ?? '', {
    responseType: 'blob',
  }).catch(() => loadingExport.value = false);
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'Data.xlsx');
  document.body.appendChild(link);
  link.click();
  loadingExport.value = false;
}
</script>
