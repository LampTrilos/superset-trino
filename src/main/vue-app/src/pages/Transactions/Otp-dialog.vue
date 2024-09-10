<template>
  <q-form @submit="onSubmit">
  <div class="q-pa-md column justify-center items-center">
    <h6  class="justify-center q-mb-none">Πρόκειται να υπογράψετε τις εξής εγγραφές</h6>
    <ul>
      <!-- Use v-for to iterate through the array -->
      <li v-for="(item, index) in rowIdsArray" :key="index">
        {{ item }}
      </li>
    </ul>
    <div class="custom-separator"></div>
<!--    <q-separator color="orange" vertical inset style="height: 15px"/>-->
    <h5  class="justify-center q-mt-md">Παρακαλώ εισάγετε τον κωδικό που λάβατε</h5>
  <div class="row q-gutter-x-sm">
    <q-input
      outlined
      v-for="i in length"
      v-model="fieldValues[i - 1]"
      v-bind="$attrs"
      @keyup="onKeyUp($event, i - 1)"
      @update:model-value="onUpdate($event, i - 1)"
      :key="i"
      :ref="el => updateFieldRef(el, i - 1)"
      maxlength="1"
      input-class="text-center"
      style="width: 6ch">
    </q-input>
  </div>
    <q-btn
      :label="t('send')"
      :disable="!buttonEnabled"
      type="submit"
      color="primary"
      no-caps
      unelevated
      class="q-mr-sm q-ma-md"
    />
</div>
  </q-form>
</template>
<script setup>
import {onBeforeUpdate, ref, watch, computed} from "vue";
import {Loading, Notify} from "quasar";
import { useI18n } from 'vue-i18n';
import {insertOrUpdateSealingApp, validateOTPviaOTPPage} from "src/services/transactions";
import { useRoute, useRouter } from 'vue-router';
const route = useRoute();
const router = useRouter();
const { t } = useI18n();
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query';
const queryClient = useQueryClient();
import {SignRequestClass} from "src/services/models";
  //------------------------------------Custom field section--------------------------------------------//
  // const length = 6;
  const length = ref(6);
  const fields = ref([]);
  const fieldValues = ref([]);

  //The whole code from all fields
  const composite = computed(() => {
    const nonNullFields = fieldValues.value.filter((value) => value);
    if (length.value !== nonNullFields.length) {
      return "";
    }
    return nonNullFields.join("");
  });

    if (composite.value) {
      // You should emit this value, e.g.
      //emit("update:modelValue", composite.value);
      Notify.create({
        message: `New input: ${composite.value}`,
        type: "positive"
      });

      console.log(composite.value);
    }
  // make sure to reset the refs before each update
  onBeforeUpdate(() => {
    fields.value = [];
  });

  const updateFieldRef = (element, index) => {
    if (element) {
      fields.value[index] = element;
    }
  };

  const focus = (index) => {
    if (index >= 0) {
      if (index < length.value) {
        fields.value[index].select();
      } else {
        if (composite.value) {
          fields.value[index - 1].blur();
        }
      }
    }
  };

  const onUpdate = (value, index) => {
    // console.log( 'sadfsdf')
    // console.log(index)
    if (value) {
      focus(index + 1);
    }
  };

  const onKeyUp = (evnt, index) => {
    const key = evnt.key;

    if (["Tab", "Shift", "Meta", "Control", "Alt"].includes(key)) {
      return;
    }

    if (["Delete"].includes(key)) {
      return;
    }

    if (key === "ArrowLeft" || key === "Backspace") {
      focus(index - 1);
    } else if (key === "ArrowRight") {
      focus(index + 1);
    }
  };

  //Submit button is enabled only if all digits are entered
  const buttonEnabled = computed(() => {
    return composite.value.length >= 6
  })
//------------------------------------End of Custom field section--------------------------------------------//
//------------------------------------Parameters passed in the url section--------------------------------------------//
const userId = computed(() => {
  return route.query.userId;
})
//(i.e vas-122 or signals-12)
const templateCode = computed(() => {
  return route.query.templateCode;
})
//The actual id of the record in its respective application
const rowId = computed(() => {
  //console.log(route.query.rowId)
  return route.query.rowId;
})
//If there are multiple ids to be signed, let's make a new array so we can show them on screen
const rowIdsArray = computed(() => {
  //if (rowId.value.includes(',')) {
    return rowId.value.split(',')
  // }
  // return rowId.split(',');
})

//The callback url must absolutely contain the word rowId, so it can be replaced dynamically
const callbackUrl = computed(() => {
  let dynamicCallbackUrl = route.query.callbackUrl.replace('rowId', rowId.value);
  //console.log(dynamicCallbackUrl)
  return decodeURIComponent(dynamicCallbackUrl);
  //return dynamicCallbackUrl;
})
//------------------------------------End of Parameters passed in the url section--------------------------------------------//
  //For the submit part
// const { mutate: postM } = useMutation(validateOTPviaOTPPage, {
//   onMutate: () => {
//     Loading.show();
//   },
//   onSettled: () => {
//     Loading.hide();
//   },
// });
function onSubmit() {
  //We need to create a new SignRequest object that contains all the necessary data, so the match can be made with templateCode, rowId and verificationCode
  //let signRequestFromOtp = new SignRequestClass('as','275330', 'asdasd@sad.com', '', null, 'vas-122', 1, '11223344', composite.value )
  let signRequestFromOtp = new SignRequestClass('as',userId.value, 'asdasd@sad.com', '', null, templateCode.value, 1, rowId.value, composite.value )
  //postM(signRequestFromOtp, callbackUrl.value);
  validateOTPviaOTPPage(signRequestFromOtp, callbackUrl.value);
}

</script>
<style scoped>
.custom-separator {
  height: 1px; /* Adjust height as needed */
  background-color: #1838a4; /* Custom color */
  margin: 1rem 0; /* Adjust spacing as needed */
  width: 100%; /* Full width of the container */
  margin-bottom: 0;
}
</style>
