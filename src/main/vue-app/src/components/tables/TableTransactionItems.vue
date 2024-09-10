<template>
  <div v-if="editable" class="row q-col-gutter-sm q-mt-md q-mb-md">
    <div class="col-12 col-md-4">
      <div class="q-gutter-sm">
        <q-btn-dropdown
          color="secondary"
          icon="add"
          unelevated
          no-caps
          outline
          @click="add"
          data-cy="add-tr-item"
          split
          :label="t('add') + ' ' + t('item', 2)"
        >
          <q-list bordered separator>
            <q-item clickable v-ripple v-close-popup>
              <q-item-section @click="dialogTemplates = true">{{
                'Προσθήκη από Πρότυπα Κωδικών & Ποσοτήτων'
              }}</q-item-section>
            </q-item>
          </q-list>
        </q-btn-dropdown>
      </div>
    </div>
  </div>
  <q-table
    :columns="columns"
    :rows="itransactions"
    row-key="key"
    :rows-per-page-options="[50, 100]"
    :flat="true"
    :visible-columns="visibleColumns"
    :pagination="{
      sortBy: 'item',
    }"
    :wrap-cells="true"
  >
    <template #header="propsHeader">
      <q-tr :props="propsHeader">
        <q-th auto-width></q-th>
        <q-th
          v-for="col in propsHeader.cols"
          :key="col.name"
          :props="propsHeader"
          :style="col.style"
        >
          {{ col.label }}
        </q-th>
      </q-tr>
    </template>

    <!--    eslint-disable-next-line vue/no-template-shadow-->
    <template #body="props">
      <q-tr :props="props">
        <q-td class="q-px-xs">
          <q-btn
            v-if="
              props.row.childrenTransactions &&
              props.row.childrenTransactions.length > 0 &&
              !(compTransaction.type.id == 'T00' && ['YPODIMA', 'ROUCHO', 'PANTELONI'].includes(props.row.itemCodeEntity.template))
            "
            size="md"
            unelevated
            round
            @click="props.expand = !props.expand"
            :icon="props.expand ? 'expand_less' : 'expand_more'"
          ></q-btn>
        </q-td>
        <q-td
          v-for="col in props.cols.filter((obj) => obj.name !== 'actions')"
          :key="col.name"
          :props="props"
        >
          <router-link
            v-if="
              col.name == 'serialNumber' && props.row.itemJson?.id && !editable
            "
            class="text-primary"
            :to="{ name: myItems ? 'MyItemView' : 'ItemView', params: { id: props.row.itemJson?.id } }"
            >{{ col.value }}</router-link
          >
          <div v-else>
            {{ col.value }}
          </div>
        </q-td>
        <q-td :props="props" :key="'actions'">
          <q-btn
            v-if="editable"
            round
            flat
            size="sm"
            icon="sym_o_content_copy"
            @click="copy(props.row)"
            ><q-tooltip>{{ t('copy') }}</q-tooltip></q-btn
          >
          <q-btn
            v-if="editable"
            round
            flat
            size="sm"
            icon="sym_o_edit"
            @click="edit(props.row)"
            ><q-tooltip>{{ t('edit') }}</q-tooltip></q-btn
          >
          <q-btn
            v-if="editable"
            round
            flat
            size="sm"
            icon="mdi-delete-outline"
            @click="remove(props.row)"
            ><q-tooltip>{{ t('delete') }}</q-tooltip></q-btn
          >
        </q-td>
      </q-tr>
      <q-tr
        v-if="
          props.expand &&
          props.row.childrenTransactions &&
          props.row.childrenTransactions.length > 0 &&
          !(compTransaction.type.id == 'T00' && ['YPODIMA', 'ROUCHO', 'PANTELONI'].includes(props.row.itemCodeEntity.template))
        "
        :props="props"
      >
        <q-td colspan="100%">
          <q-table
            :rows="props.row.childrenTransactions"
            :columns="columns"
            row-key="key"
            :rows-per-page-options="[10]"
            :visible-columns="visibleColumns"
          >
            <template #body="propsBody">
              <q-tr :props="propsBody">
                <q-td
                  v-for="col in propsBody.cols
                    .filter((cl) => visibleColumns.includes(cl.name))
                    .filter((obj) => obj.name !== 'actions')"
                  :key="col.name"
                  :props="propsBody"
                >
                  <router-link
                    v-if="
                      col.name == 'serialNumber' &&
                      propsBody.row.itemJson?.id &&
                      !editable
                    "
                    class="text-primary"
                    :to="{
                      name: myItems ? 'MyItemView' : 'ItemView',
                      params: { id: propsBody.row.itemJson?.id },
                    }"
                    >{{
                      col.value ? col.value : 'Χωρίς Σειριακό Αριθμό'
                    }}</router-link
                  >
                  {{
                    (col.name == 'serialNumber' && editable) ||
                    col.name != 'serialNumber'
                      ? col.value
                      : ''
                  }}
                  <q-popup-edit
                    v-if="
                      compTransaction.type.id == 'T00' &&
                      col.name == 'serialNumber' &&
                      editable
                    "
                    v-model="propsBody.row.itemJson.serialNumber"
                    title="Επεξεργασία Σειριακού Αριθμού"
                    buttons
                    v-slot="scope"
                  >
                    <q-input v-model="scope.value" dense autofocus />
                  </q-popup-edit>
                </q-td>
                <q-td
                  v-if="propsBody.cols.some((obj) => obj.name === 'actions')"
                  :props="propsBody"
                  :key="'actions'"
                >
                  <q-btn
                    v-if="editable"
                    round
                    flat
                    size="sm"
                    icon="mdi-delete-outline"
                    @click="removeChildren(propsBody.row, props.row)"
                    ><q-tooltip>{{ t('delete') }}</q-tooltip></q-btn
                  >
                </q-td>
              </q-tr>
              <q-tr
                v-if="propsBody.expand"
                :props="propsBody"
                class="bg-grey-2"
              >
                <q-td colspan="100%">
                  <slot name="expandedSlot" :row="propsBody.row"></slot>
                </q-td>
              </q-tr>
            </template>
          </q-table>
        </q-td>
      </q-tr>
    </template>
  </q-table>
  <q-dialog v-model="itemDialog" persistent>
    <q-card style="min-width: 80vw; max-width: 80vw;">
      <q-card-section> {{ t('item', 0) }} </q-card-section>
      <q-card-section>
        <q-form @submit="save">
          <div class="row q-col-gutter-sm" v-if="selectChildDepUnits">
            <div class="col-12 col-md-12">
              <SelectDepartment
                v-model.lazy="editedItem[accountEntity]"
                :label="t(accountEntity)"
                :parent-dep-unit="compTransaction[accountEntity]?.id"
                class="required"
                :rules="requiredRule()"
                use-chips
                :manage="true"
                :readonly="!editable"
                :fetch-data="editable"
                :data-cy="'dialog-' + accountEntity"
              />
            </div>
          </div>
          <div
            class="row q-col-gutter-sm"
            v-if="
              !selectChildDepUnits ||
              (selectChildDepUnits && editedItem[accountEntity] != null)
            "
          >
            <div class="col-12 col-md-12">
              <SelectItemCode
                v-model="editedItem.itemCodeEntity"
                class="required"
                :rules="requiredRule()"
                :readonly="!editable"
                :fetch-data="editable"
                :inactive="false"
                data-cy="dialog-selectItemCode"
              ></SelectItemCode>
            </div>
            <div
              class="col-12 col-md-12"
              v-if="editedItem.itemCodeEntity != null"
            >
              <InputNumber
                filled
                :label="t('amount', 0)"
                clearable
                v-model="editedItem.amount"
                :placeholder="editedItem.itemCodeEntity.discreet ? '#' : '#,######'"
                class="required"
                :readonly="!editable"
                :rules="editedItem.itemCodeEntity.discreet ? amountRuleDiscreet() : amountRule()"
                data-cy="dialog-amount"
              />
            </div>
            <div
              class="col-12 col-md-12"
              v-if="
                editedItem.itemCodeEntity != null &&
                editedItem.itemCodeEntity.discreet &&
                itemsAssignedToDepUnit &&
                editedItem.amount <= 1
              "
            >
              <SelectItem
                :key="
                  compTransaction.type?.id +
                  '-' +
                  editedItem.itemCodeEntity?.id +
                  '-' +
                  (selectChildDepUnits
                    ? editedItem.fromAccountEntity.id
                    : compTransaction.fromAccountEntity?.id)
                "
                v-model="editedItem.itemJson"
                :item-code="editedItem.itemCodeEntity?.id"
                :dep-unit="
                  itemsAssignedToUser
                    ? null
                    : selectChildDepUnits
                    ? editedItem.fromAccountEntity.id
                    : compTransaction.fromAccountEntity?.id
                "
                :assigned-to-dep-unit="true"
                :assigned-to-user="
                  selectChildDepUnits ? false : itemsAssignedToUser
                "
                :assignee-user="compTransaction.fromUser?.id"
                :assignee-ou="
                  compTransaction.fromOu
                    ? compTransaction.fromOu?.id
                    : compTransaction.toOu && props.itemsAssignedToOu
                    ? compTransaction.toOu?.id
                    : null
                "
                :assigned-to-ou="props.itemsAssignedToOu"
                :assigned-to-topikos-ypeythynos="compTransaction.fromOu ? true : (compTransaction.toOu ? false : null)"
                :manage="
                  compTransaction.fromOu != null
                    ? compTransaction.toAccountEntity
                      ? null
                      : true
                    : null
                "
                class="required"
                :rules="requiredRule()"
                :readonly="
                  !editable ||
                  (compTransaction.fromAccountEntity?.id == null &&
                    compTransaction.fromUser?.id == null &&
                    compTransaction.fromOu?.id == null)
                "
                :fetch-data="editable"
                :in-transaction="false"
                :temporal-reserved="false"
                data-cy="dialog-item"
              ></SelectItem>
              <q-banner
                v-if="
                  compTransaction.fromAccountEntity?.id == null &&
                  compTransaction.fromUser?.id == null &&
                  compTransaction.fromOu?.id == null
                "
                class="bg-warning text-white"
                >Επιλέξτε από λογαριασμό υπηρεσίας για να εμφανιστούν τα
                διαθέσιμα υλικά</q-banner
              >
            </div>
            <div
              class="col-12 col-md-12"
              v-if="
                editable &&
                editedItem.itemCodeEntity != null &&
                editedItem.itemCodeEntity.discreet &&
                itemsAssignedToDepUnit &&
                editedItem.amount > 1
              "
            >
              <TableItems
                v-if="
                  compTransaction.fromAccountEntity?.id != null ||
                  compTransaction.fromUser?.id != null ||
                  compTransaction.fromOu?.id != null
                "
                :key="
                  compTransaction.type?.id +
                  '-' +
                  editedItem.itemCodeEntity?.id +
                  '-' +
                  (selectChildDepUnits
                    ? editedItem.fromAccountEntity.id
                    : compTransaction.fromAccountEntity?.id) +
                  '-' +
                  compTransaction.fromOu?.id
                "
                :item-code="editedItem.itemCodeEntity"
                :assigned-to-dep-unit="true"
                :assigned-to-user="itemsAssignedToUser"
                :assigned-to-ou="props.itemsAssignedToOu"
                :dep-unit="
                  itemsAssignedToUser
                    ? null
                    : selectChildDepUnits
                    ? editedItem.fromAccountEntity.id
                    : compTransaction.fromAccountEntity?.id
                "
                :assignee-user="compTransaction.fromUser?.id"
                :assignee-ou="
                  compTransaction.fromOu
                    ? compTransaction.fromOu?.id
                    : compTransaction.toOu && props.itemsAssignedToOu
                    ? compTransaction.toOu?.id
                    : null
                "
                :assigned-to-topikos-ypeythynos="compTransaction.fromOu ? true : (compTransaction.toOu ? false : null)"
                :manage="
                  compTransaction.fromOu != null
                    ? compTransaction.toAccountEntity
                      ? null
                      : true
                    : null
                "
                :include-children-units="false"
                :selection-enabled="true"
                :initial-selected="
                  editedItem.childrenTransactions == null
                    ? []
                    : editedItem.childrenTransactions.map((chl) => ({
                        id: chl.itemJson.id.toString(),
                        serialNumber: chl.itemJson.serialNumber,
                      }))
                "
                :in-transaction="false"
                :temporal-reserved="false"
                v-model="editedItem.childrenTransactionsItems"
              />
              <q-banner
                v-if="
                  compTransaction.fromAccountEntity?.id == null &&
                  compTransaction.fromUser?.id == null &&
                  compTransaction.fromOu?.id == null
                "
                class="bg-warning text-white"
                >Επιλέξτε από λογαριασμό υπηρεσίας για να εμφανιστούν τα
                διαθέσιμα υλικά</q-banner
              >
            </div>
            <div
              class="col-12 col-md-12"
              v-if="
                editedItem.itemCodeEntity != null &&
                editedItem.itemCodeEntity.discreet &&
                editedItem.itemCodeEntity.template &&
                !itemsAssignedToDepUnit
              "
            >
              <ItemYliko
                :key="editedItem.itemCodeEntity.template"
                :template="editedItem.itemCodeEntity.template"
                v-model="editedItem.itemJson"
                :editable="editable"
                :multiple-items="editedItem.amount > 1"
                :metaptosi="false"
                :kae="editedItem.itemCodeEntity.code"
                :itemCodeId="editedItem.itemCodeEntity?.id"
                :dep-unit-code="editedItem.toAccountEntity?.code"
              />
            </div>
            <AlertAvailableAmount
              v-if="
                editedItem.itemCodeEntity != null &&
                compTransaction.type.id != 'T00'
              "
              :key="
                compTransaction.type?.id +
                '-' +
                editedItem.itemCodeEntity?.id +
                '-' +
                (selectChildDepUnits
                  ? editedItem.fromAccountEntity?.id
                  : compTransaction.fromAccountEntity?.id) +
                '-' +
                compTransaction.toAccountEntity?.id +
                '-' +
                compTransaction.fromOu?.id +
                '-' +
                compTransaction.toOu?.id +
                '-' +
                compTransaction.fromUser?.id
              "
              :transaction-type="compTransaction.type.id"
              :item-code="editedItem.itemCodeEntity?.id"
              :dep-unit-from-id="
                itemsAssignedToUser
                  ? null
                  : selectChildDepUnits
                  ? editedItem.fromAccountEntity?.id
                  : compTransaction.fromAccountEntity?.id
              "
              :dep-unit-to-id="compTransaction.toAccountEntity?.id"
              :ou-from-id="compTransaction.fromOu?.id"
              :ou-to-id="compTransaction.toOu?.id"
              :user-from-id="compTransaction.fromUser?.id"
            />
          </div>
          <div class="q-mt-md">
            <q-btn
              v-if="editable"
              type="submit"
              :label="t('save')"
              color="primary"
              no-caps
              unelevated
              data-cy="dialog-save"
            />
            <q-btn
              class="q-ml-sm"
              :label="t('close')"
              outline
              no-caps
              v-close-popup
            />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-dialog>
  <q-dialog v-model="dialogTemplates">
    <q-card style="min-width: 500px">
      <q-card-section>
        <SelectEnum
          filled
          v-model="transactionTemplate"
          :label="t('transactionTemplate', 2)"
          clearable
          enum-end-point="transaction-templates"
        />
      </q-card-section>
      <q-card-actions align="left">
        <q-btn
          :label="t('select')"
          color="primary"
          @click="onSelectTemplate"
          no-caps
          unelevated
          :disable="!transactionTemplate"
        />
        <q-btn :label="t('close')" outline v-close-popup no-caps />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { ITransaction } from 'src/services/models';
import {defineAsyncComponent, reactive, ref, watch} from 'vue';
import { useValidationRules } from 'src/utils/validation-rules';
import SelectItemCode from 'components/selects/SelectItemCode.vue';
import SelectItem from 'components/selects/SelectItem.vue';
import { useCodeFormatter } from 'src/utils/code-formatter';
import SelectDepartment from 'components/selects/SelectDepartment.vue';
import { Loading } from 'quasar';
import TableItems from 'components/tables/TableItems.vue';
import AlertAvailableAmount from 'components/alerts/AlertAvailableAmount.vue';
import SelectEnum from 'components/selects/SelectEnum.vue';
import InputNumber from 'components/inputs/InputNumber.vue';
const { formatItemCodeCategory, formatItemCode, formatDep } =
  useCodeFormatter();

import { useNumberFormatter } from 'src/utils/number-formatter';
import ItemYliko from "components/items/ItemYliko.vue";
const { englishToGreekNumber } = useNumberFormatter();

const { t } = useI18n();
const props = defineProps({
  modelValue: Array,
  compTransaction: Object,
  itemsAssignedToDepUnit: {
    type: Boolean,
    default: true,
  },
  itemsAssignedToUser: {
    type: Boolean,
    default: false,
  },
  itemsAssignedToOu: {
    type: Boolean,
    default: null,
  },
  editable: Boolean,
  selectChildDepUnits: {
    type: Boolean,
    default: false,
  },
  accountEntity: {
    type: String,
    default: 'fromAccountEntity',
  },
  myItems: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue']);
const columns = [
  /*  {
    name: 'key',
    field: 'key',
    required: true,
    label: t('key'),
    align: 'left',
  },*/
  {
    name: props.accountEntity,
    label: t(props.accountEntity),
    align: 'left',
    field: (row: ITransaction) =>
      props.accountEntity == 'fromAccountEntity' && row.proxyDepUnitFrom != null
        ? '(' +
          formatDep(row.proxyDepUnitFrom.code) +
          ') ' +
          (row.proxyDepUnitFrom?.name ?? '')
        : props.accountEntity == 'toAccountEntity' && row.proxyDepUnitTo != null
        ? '(' +
          formatDep(row.proxyDepUnitTo.code) +
          ') ' +
          (row.proxyDepUnitTo?.name ?? '')
        : row[props.accountEntity] != null
        ? '(' +
          formatDep(row[props.accountEntity].code) +
          ') ' +
          (row[props.accountEntity]?.name ?? '')
        : '',
    sortable: true,
    style: 'min-width: 250px;',
  },
  {
    name: 'item',
    field: (row: ITransaction) =>
      '(' +
      formatItemCodeCategory(row.itemCodeEntity.code?.split(' ')[0]) +
      ' ' +
      formatItemCode(row.itemCodeEntity.code?.split(' ')[1]) +
      ') ' +
      (row.itemCodeEntity.name ?? ''),
    label: t('codeItem', 0),
    required: true,
    align: 'left',
    style: 'min-width: 250px;',
    sortable: true,
  },
  {
    name: 'amount',
    required: true,
    label: t('amount'),
    align: 'left',
    field: (row: ITransaction) => englishToGreekNumber(row.amount),
    sortable: true,
  },
  {
    name: 'serialNumber',
    field: (row: ITransaction) => row.itemJson?.serialNumber,
    required: true,
    label: t('serialNumber', 0),
    align: 'left',
    sortable: true,
  },
  {
    name: 'actions',
    style: 'min-width: 150px;',
  },
];

let itransactions = reactive(props.modelValue);

let editedIndex = ref(-1);

const defaultItem = {
  itemCodeEntity: null,
  amount: 1,
};

let editedItem: ITransaction = reactive({ ...defaultItem });

let itemDialog = ref(false);
function add() {
  console.log('New itransaction');
  editedItem = reactive(Object.assign({}, defaultItem));
  if (!props.itemsAssignedToDepUnit) {
    editedItem.itemJson = {};
  }
  editedItem.key = Date.now();
  editedIndex.value = -1;
  itemDialog.value = true;
}

function edit(row) {
  editedIndex.value = itransactions.indexOf(row);
  editedItem = reactive(Object.assign({}, row));
  editedItem.childrenTransactionsItems =
    editedItem.childrenTransactions == null
      ? []
      : editedItem.childrenTransactions.map((chl) => ({
          id: chl.itemJson.id,
          serialNumber: chl.itemJson.serialNumber,
        }));
  itemDialog.value = true;
}

function save() {
  Loading.show();
  if (editedIndex.value > -1) {
    if (editedItem.itemCodeEntity?.discreet && editedItem.amount > 1) {
      if (props.itemsAssignedToDepUnit || props.itemsAssignedToUser) {
        let initialCopy = JSON.stringify(editedItem);
        if (!editedItem.childrenTransactionsItems) {
          editedItem.childrenTransactionsItems = [];
        }
        editedItem.childrenTransactions = [];
        for (let i = 0; i < editedItem.childrenTransactionsItems.length; i++) {
          let copiedEditedItem = JSON.parse(initialCopy);
          delete copiedEditedItem.childrenTransactions;
          delete copiedEditedItem.childrenTransactionsItems;
          copiedEditedItem.itemJson = JSON.parse(
            JSON.stringify(editedItem.childrenTransactionsItems[i])
          );
          copiedEditedItem.amount = 1;
          editedItem.childrenTransactions.push(copiedEditedItem);
        }
        delete editedItem.childrenTransactionsItems;
        itransactions[editedIndex.value].childrenTransactions = [];
        Object.assign(itransactions[editedIndex.value], editedItem);
      } else {
        console.log('here');
        editedItem.childrenTransactions = [];
        let initialCopy = JSON.stringify(editedItem);
        let oldChildrenLength = itransactions[editedIndex.value]
          .childrenTransactions
          ? itransactions[editedIndex.value].childrenTransactions.length
          : 0;
        let serialNumbers =
          editedItem.itemJson?.serialNumbers == null
            ? []
            : editedItem.itemJson.serialNumbers.split(',');
        for (let i = 0; i < editedItem.amount; i++) {
          let copiedEditedItem = JSON.parse(initialCopy);
          delete copiedEditedItem.childrenTransactions;
          copiedEditedItem.amount = 1;
          if (copiedEditedItem.itemJson) {
            delete copiedEditedItem.itemJson.serialNumbers;
            copiedEditedItem.itemJson.serialNumber =
              i < oldChildrenLength
                ? itransactions[editedIndex.value].childrenTransactions[i]
                    .itemJson.serialNumber
                : serialNumbers[i - oldChildrenLength];
          }
          copiedEditedItem.key = Date.now() + i;
          editedItem.childrenTransactions.push(copiedEditedItem);
        }
        if (editedItem.itemJson) {
          editedItem.itemJson.serialNumbers = null;
          editedItem.itemJson.serialNumber = null;
        }
        itransactions[editedIndex.value].childrenTransactions = [];
        Object.assign(itransactions[editedIndex.value], editedItem);
      }
      //delete itransactions[editedIndex.value].itemJson;
    } else {
      if (!editedItem.itemCodeEntity?.discreet) {
        delete editedItem.itemJson;
      }
      Object.assign(itransactions[editedIndex.value], editedItem);
    }
  } else {
    if (
      editedItem.itemCodeEntity?.discreet &&
      editedItem.itemJson == null &&
      !props.itemsAssignedToDepUnit &&
      !props.itemsAssignedToUser
    ) {
      editedItem.itemJson = {};
    }
    if (editedItem.itemCodeEntity?.discreet && editedItem.amount > 1) {
      editedItem.childrenTransactions = [];
      if (props.itemsAssignedToDepUnit || props.itemsAssignedToUser) {
        let initialCopy = JSON.stringify(editedItem);
        if (!editedItem.childrenTransactionsItems) {
          editedItem.childrenTransactionsItems = [];
        }
        for (let i = 0; i < editedItem.childrenTransactionsItems.length; i++) {
          let copiedEditedItem = JSON.parse(initialCopy);
          delete copiedEditedItem.childrenTransactions;
          delete copiedEditedItem.childrenTransactionsItems;
          copiedEditedItem.itemJson = JSON.parse(
            JSON.stringify(editedItem.childrenTransactionsItems[i])
          );
          copiedEditedItem.amount = 1;
          editedItem.childrenTransactions.push(copiedEditedItem);
        }
        delete editedItem.childrenTransactionsItems;
        itransactions.push(editedItem);
      } else {
        let initialCopy = JSON.stringify(editedItem);
        let serialNumbers =
          editedItem.itemJson?.serialNumbers == null
            ? []
            : editedItem.itemJson.serialNumbers.split(',');
        for (let i = 0; i < editedItem.amount; i++) {
          let copiedEditedItem = JSON.parse(initialCopy);
          delete copiedEditedItem.childrenTransactions;
          if (copiedEditedItem.itemJson) {
            delete copiedEditedItem.itemJson.serialNumbers;
            copiedEditedItem.itemJson.serialNumber = serialNumbers[i];
          }
          copiedEditedItem.amount = 1;
          copiedEditedItem.key = Date.now() + i;
          editedItem.childrenTransactions.push(copiedEditedItem);
        }
        if (editedItem.itemJson) {
          editedItem.itemJson.serialNumbers = null;
          editedItem.itemJson.serialNumber = null;
        }
        itransactions.push(editedItem);
      }
    } else {
      if (!editedItem.itemCodeEntity?.discreet) {
        delete editedItem.itemJson;
      }
      itransactions.push(editedItem);
    }
  }
  itemDialog.value = false;
  Loading.hide();
}

function remove(row) {
  editedIndex.value = itransactions.indexOf(row);
  itransactions.splice(editedIndex.value, 1);
}

function removeChildren(row, parentTransaction) {
  let index = parentTransaction.childrenTransactions.indexOf(row);
  parentTransaction.childrenTransactions.splice(index, 1);
  parentTransaction.amount--;
}

function copy(row) {
  let copiedItem = JSON.parse(JSON.stringify(row));
  copiedItem.key = Date.now();
  itransactions.push(copiedItem);
}

watch(
  () => itransactions,
  () => {
    emit('update:modelValue', itransactions);
  },
  { deep: true }
);

const { requiredRule, amountRuleDiscreet, amountRule } = useValidationRules();

let visibleColumns = ref(['item', 'amount', 'serialNumber', 'actions']);
if (props.selectChildDepUnits) {
  visibleColumns.value.push(props.accountEntity);
}

let dialogTemplates = ref(false);
let transactionTemplate = ref(null);
function onSelectTemplate() {
  for (const record of transactionTemplate.value.records) {
    console.log(record);
    let recordItem = {
      key: Date.now(),
      itemCodeEntity: record.itemCode,
      amount: record.amount,
    };
    if (record.itemCode.discreet) {
      if (props.compTransaction.type.id == 'T00') {
        recordItem.itemJson = {};
      }

      if (recordItem.amount > 1) {
        recordItem.childrenTransactions = [];
      }
    }
    console.log(recordItem);
    itransactions.push(recordItem);
  }
  dialogTemplates.value = false;
}

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

function is() {
  const template = editedItem.itemCodeEntity?.template.toLowerCase();
  console.log(template)
  if (template && template in templateComponentMap) {
    return defineAsyncComponent(templateComponentMap[template]);
  } else {
    return null; // Default case if template is not found or not handled
  }
}
</script>
