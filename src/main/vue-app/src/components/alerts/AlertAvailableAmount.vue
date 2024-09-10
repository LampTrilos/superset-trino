<template>
  <div class="text-caption" v-if="availableAmount != null">
    {{ 'Διαθέσιμη ποσότητα υλικού: ' + englishToGreekNumber(availableAmount) }}
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Id } from 'src/services/types';
import axios from 'axios';
import { Item, PageResponse } from 'src/services/models';
import { useNumberFormatter } from 'src/utils/number-formatter';
const { englishToGreekNumber } = useNumberFormatter();

const props = withDefaults(
  defineProps<{
    transactionType: string;
    itemCode: Id;
    depUnitToId?: Id;
    depUnitFromId?: Id;
    userFromId?: Id;
    ouFromId?: Id;
    ouToId?: Id;
  }>(),
  {
    itemCode: null,
    depUnitToId: null,
    depUnitFromId: null,
    userFromId: null,
    ouFromId: null,
    ouToId: null,
  }
);

let availableAmount = ref(null);

const getItemsOptions = async () => {
  const response = await axios.get<PageResponse<Item>>(
    '/api/v1/balances/by-transaction-type' +
      '?' +
      'type=' +
      props.transactionType +
      '&itemCode=' +
      props.itemCode +
      (props.depUnitToId != null ? '&depUnitTo=' + props.depUnitToId : '') +
      (props.depUnitFromId != null
        ? '&depUnitFrom=' + props.depUnitFromId
        : '') +
      (props.userFromId != null ? '&userFrom=' + props.userFromId : '') +
      (props.ouFromId != null ? '&ouFrom=' + props.ouFromId : '') +
      (props.ouToId != null ? '&ouTo=' + props.ouToId : '')
  );
  availableAmount.value = response.data.balance;
};

getItemsOptions();
</script>

<style lang="sass"></style>
