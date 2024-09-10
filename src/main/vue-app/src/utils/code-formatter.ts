import {Ou, User} from 'src/services/models';

export function useCodeFormatter() {
  function formatItemCode(code: string): string {
    if (!code) {
      return '';
    }
    return code.slice(-3);
  }

  function formatItemCodeCategory(code: string): string {
    if (!code) {
      return '';
    }
    return code.slice(-2);
  }

  function formatItemCodeFull(code: string) {
    if (code == null || code == undefined) {
      return '';
    }
    const parts = code.split(' ');
    let formattedString = '';
    if (parts.length > 0) {
      formattedString += formatItemCodeCategory(parts[0]);
    }
    if (parts.length > 1) {
      formattedString += ' ' + formatItemCode(parts[1]);
    }
    return formattedString;
  }

  function formatGenDepUnit(code: string): string {
    if (!code) {
      return '';
    }
    return code.slice(-2);
  }

  function formatParDepUnit(code: string): string {
    if (!code) {
      return '';
    }
    return code.slice(-3);
  }

  function formatDep(code: string) {
    if (code == null || code == undefined) {
      return '';
    }
    const parts = code.split(' ');
    let formattedString = '';
    if (parts.length > 0) {
      formattedString += formatGenDepUnit(parts[0]);
    }
    if (parts.length > 1) {
      formattedString += ' ' + formatParDepUnit(parts[1]);
    }
    return formattedString;
  }

  function transformStringWithLeadingZeros(str: string, length: number) {
    // Remove leading zeros from the original string
    const strippedStr = str.replace(/^0+(?!$)/, '');

    // Calculate the number of zeros to be added
    const numZerosToAdd = length - strippedStr.length;

    // Construct the transformed string with leading zeros
    const transformedStr = '0'.repeat(numZerosToAdd) + strippedStr;

    return transformedStr;
  }

  function transformCodeWithLeadingZeros(code: string, length: number) {
    if (code == null || code.trim() === '') {
      return '';
    }
    const parts = code.split(' ');
    const formattedParts = parts.map((part) =>
      transformStringWithLeadingZeros(part.trim(), length)
    );
    const formattedString = formattedParts.join(' ');
    return formattedString.trim();
  }

  function formatUser(user: User) {
    if (!user) {
      return '';
    }
    return (
      (user.arithmosMitroou ? ' (' + user.arithmosMitroou + ') ' : '') +
      user.lastName +
      ' ' +
      (user.firstName ? user.firstName : '')
    );
  }

  function formatOu(ou: Ou): string {
    return ou.mitroo ? '(' + ou.mitroo + ') ' + ou.name : ou.name;
  }

  return {
    formatItemCode,
    formatItemCodeCategory,
    formatItemCodeFull,
    formatDep,
    formatUser,
    formatParDepUnit,
    transformCodeWithLeadingZeros,
    formatOu,
  };
}
