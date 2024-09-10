import { useI18n } from 'vue-i18n';

export function useValidationRules() {
  const { t } = useI18n();
  // Required rules
  function requiredRule() {
    return [
      (v) => (v !== null && v !== undefined && v !== '') || t('requiredField'),
    ];
  }

  function requiredRuleOchima() {
    return [
      (v) => (v !== null && v !== undefined && v != 'Δεν επαληθεύτηκε') || 'Η επαλήθευση της πινακίδας από το σύστημα οχήματα είναι υποχρεωτική',
    ];
  }

  function amountRule() {
    // Regular expression pattern to match decimal numbers with up to 8 digits before the decimal point
    // and up to 2 digits after the decimal point
    const pattern = /^(\d{1,8})(\,\d{0,6})?$/;
    //const isValid = pattern.test(inputValue);

    //const num = parseFloat(inputValue);
    //console.log(!isNaN(num) && num > 0);
    return [
      (v) =>
        (v !== null &&
          v !== undefined &&
          v !== '' &&
          v !== '0' &&
          pattern.test(v)) ||
        t('amountRule'),
    ];
  }

  function amountRuleDiscreet() {
    // Regular expression pattern to match decimal numbers with up to 8 digits before the decimal point
    // and up to 2 digits after the decimal point
    const pattern = /^(\d{1,8})$/;

    return [
      (v) => {
        const isValidFormat = (v !== null && v !== undefined && v !== '' && v !== '0' && pattern.test(v));
        const isValidRange = parseFloat(v.replace(',', '.')) <= 1000;

        return (isValidFormat && isValidRange) || t('amountRuleDiscreet');
      }
    ];
  }

  function aleksisfairoSerialRule() {
    // Regular expression pattern to match decimal numbers with up to 8 digits before the decimal point
    // and up to 2 digits after the decimal point
    const pattern = /^[A-Z0-9.!?]+$/;
    //const isValid = pattern.test(inputValue);

    //const num = parseFloat(inputValue);
    //console.log(!isNaN(num) && num > 0);
    return [
      (v) =>
        (v !== null &&
          v !== undefined &&
          v !== '' &&
          pattern.test(v)) ||
        'Ο σειριακός αριθμός πρέπει να είναι της μορφής κεφαλαία λατινικά,αριθμούς και .!?',
    ];
  }

  function aleksisfairoSerialsRule() {
    // Regular expression pattern to match decimal numbers with up to 8 digits before the decimal point
    // and up to 2 digits after the decimal point
    const pattern = /^[A-Z0-9,.!?]+$/;
    //const isValid = pattern.test(inputValue);

    //const num = parseFloat(inputValue);
    //console.log(!isNaN(num) && num > 0);
    return [
      (v) =>
        (v !== null &&
          v !== undefined &&
          v !== '' &&
          pattern.test(v)) ||
        'Ο σειριακός αριθμός πρέπει να είναι της μορφής κεφαλαία λατινικά,αριθμούς και .!?',
    ];
  }

  function aleksisfairoSerialsRule() {
    // Regular expression pattern to match decimal numbers with up to 8 digits before the decimal point
    // and up to 2 digits after the decimal point
    const pattern = /^[A-Z0-9,.!?]+$/;
    //const isValid = pattern.test(inputValue);

    //const num = parseFloat(inputValue);
    //console.log(!isNaN(num) && num > 0);
    return [
      (v) =>
        (v !== null &&
          v !== undefined &&
          v !== '' &&
          pattern.test(v)) ||
        'Ο σειριακός αριθμός πρέπει να είναι της μορφής κεφαλαία λατινικά,αριθμούς και .!?',
    ];
  }

  function validateEANumbersRule() {
    // Regular expression pattern to match strings that start with "ΕΑ" followed by numbers
    const pattern = /^ΕΑ\d+$/;

    return [
      (v) => {
        if (v === null || v === undefined || v === '') {
          return true; // Allow null or empty strings
        } else {
          return pattern.test(v) || 'Το πεδίο πρέπει να είναι άδειο ή να ξεκινάει με "ΕΑ" και να ακολουθούν αριθμοί.';
        }
      }
    ];
  }

  function validateProtocol() {
    // Regular expression pattern to match strings that start with "/" followed by two digits, "/" and then a string
    const pattern = /\/\d+\/\d+(-.*)?$/;

    return [
      (v) => {
        if (v === null || v === undefined || v === '') {
          return t('requiredField'); // Disallow null or empty strings
        } else {
          return pattern.test(v) || 'Δεν ακολουθεί πρωτόκολλο POL';
        }
      }
    ];
  }

  return {
    requiredRule,
    amountRule,
    amountRuleDiscreet,
    aleksisfairoSerialRule,
    aleksisfairoSerialsRule,
    validateEANumbersRule,
    requiredRuleOchima,
    validateProtocol
  };
}
