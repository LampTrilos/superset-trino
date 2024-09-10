<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="(value) => emit('update:modelValue', value)"
    persistent
    full-width
  >
    <q-card>
      <q-card-section>
        <div class="text-h6">
          {{
            '(' +
            formatItemCodeCategory(itemCode.code?.split(' ')[0]) +
            ' ' +
            formatItemCode(itemCode.code?.split(' ')[1]) +
            ') ' +
            (itemCode.name ?? '')
          }}
          - {{ assignedTo }}
        </div>
      </q-card-section>
      <q-card-section>
        <TableItems
          :item-code="itemCode"
          :assigned-to-dep-unit="assignedToDepUnit"
          :assigned-to-user="assignedToUser"
          :dep-unit="depUnit?.id"
          :assignee-user="assigneeUser?.id"
          :assignee-ou="assigneeOu?.id"
          :assigned-to-ou="assignedToOu"
          :assigned-to-topikos-ypeythynos="assignedToTopikosYpeythynos"
          :include-children-units="includeChildrenUnits"
          :my-items="myItems"
          :temporal-reserved="temporalReserved"
          :in-transaction="inTransaction"
          :in-transaction-or-temporal-reserved="inTransactionOrTemporalReserved"
        />
      </q-card-section>

      <q-card-actions align="right">
        <q-btn :label="t('close')" outline v-close-popup no-caps />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useCodeFormatter } from 'src/utils/code-formatter';
import { DepUnit, ItemCode, Ou, User } from 'src/services/models';
import { computed } from 'vue';
import TableItems from 'components/tables/TableItems.vue';

const { t } = useI18n();
const { formatItemCodeCategory, formatItemCode } = useCodeFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: boolean | null;
    itemCode: ItemCode;
    depUnit: DepUnit | null;
    assigneeUser: User | null;
    assigneeOu?: Ou | null;
    assignedToDepUnit: boolean | null;
    assignedToUser: boolean | null;
    includeChildrenUnits?: boolean | null;
    myItems?: boolean | null;
    assignedToOu?: boolean | null;
    assignedToTopikosYpeythynos?: boolean | null;
    temporalReserved?: boolean | null;
    inTransaction?: boolean | null;
    inTransactionOrTemporalReserved?: boolean | null;
  }>(),
  {
    includeChildrenUnits: null,
    myItems: false,
    assignedToOu: null,
    temporalReserved: null,
    inTransaction: null,
    inTransactionOrTemporalReserved: null,
    assignedToTopikosYpeythynos: null,
  }
);

const assignedTo = computed(() => {
  if (props.depUnit) {
    return props.depUnit.name;
  } else if (props.assignedToUser) {
    return t('user-assigned');
  } else if (props.assigneeUser) {
    return (
      (props.assigneeUser.lastName ?? '') +
      ' ' +
      (props.assigneeUser.firstName ?? '')
    );
  } else {
    return t('not-assigned');
  }
});

const emit = defineEmits(['update:modelValue']);
</script>
