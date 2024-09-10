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
        <TableBalances
          :item-code="itemCode"
          :type="type ?? ''"
          :dep-unit-id="depUnit?.id"
          :assignee-user-id="assigneeUser?.id"
          :assignee-ou-id="assigneeOu?.id"
          :assigned-to-topikos-ypeythynos="assignedToTopikosYpeythynos"
        ></TableBalances>
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
import TableBalances from 'components/tables/TableBalances.vue';

const { t } = useI18n();
const { formatItemCodeCategory, formatItemCode } = useCodeFormatter();

const props = withDefaults(
  defineProps<{
    modelValue: boolean | null;
    itemCode: ItemCode;
    depUnit: DepUnit | null;
    assigneeUser: User | null;
    assignedToDepUnit: boolean | null;
    assignedToUser: boolean | null;
    includeChildrenUnits?: boolean | null;
    assigneeOu?: Ou | null;
    assignedToTopikosYpeythynos?: boolean | null;
    type?: string;
  }>(),
  {
    includeChildrenUnits: null,
    type: '',
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
