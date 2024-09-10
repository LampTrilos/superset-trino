export function useNumberFormatter() {
  function greekToEnglishNumber(num) {
    //console.log('greekToEnglishNumber ' + num)
    if (num == null) {
      return null;
    }
    const greekNumber = num.toString();
    const greekToEnglishMap = {
      '.': '',
      ',': '.',
    };
    const englishNumberArray = greekNumber
      .split('')
      .map((char) => greekToEnglishMap[char] || char);
    return englishNumberArray.join('');
  }

  function englishToGreekNumber(num) {
    //console.log('englishToGreekNumber ' + num)
    if (num == null) {
      return null;
    }
    const englishNumber = num.toString();
    const englishToGreekMap = {
      '.': ',',
      ',': '',
    };

    const greekNumberArray = englishNumber
      .split('')
      .map((char) => englishToGreekMap[char] || char);

    return greekNumberArray.join('');
  }

  function subtractDecimals(num1, num2) {
    return (num1 * 1000000 - num2 * 1000000) / 1000000;
  }

  return {
    greekToEnglishNumber,
    englishToGreekNumber,
    subtractDecimals,
  };
}
