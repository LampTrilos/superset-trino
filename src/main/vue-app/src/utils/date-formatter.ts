import moment from 'moment';
import 'moment-timezone';
import 'moment/dist/locale/el';

export function useDateFormatter() {
  function getDateFormat(): string {
    return localStorage.getItem('dateFormat') ?? 'YYYY-MM-DD';
  }

  function formatDate(date: string | null | undefined): string | null {
    if (!date) return null;
    const [year, month, day] = date.split('-');
    switch (getDateFormat()) {
      case 'YYYY-MM-DD':
        return `${year}-${month}-${day}`;
      case 'DD/MM/YYYY':
        return `${day}/${month}/${year}`;
      case 'MM/DD/YYYY':
        return `${month}/${day}/${year}`;
      default:
        return `${year}-${month}-${day}`;
    }
  }

  function formatDateTime(dateTime: string | null | undefined): string | null {
    if (!dateTime) return null;
    const [date, time] = dateTime.split('T');
    const dateFormatted = formatDate(date);
    const [hours, minutes] = time.split(':');
    return `${dateFormatted} ${hours}:${minutes}`;
  }

  function formatDateTimeLocale(
    dateTime: string | null | undefined
  ): string | null {
    if (!dateTime) return null;
    const dateTimeLocale = moment
      .utc(dateTime)
      .tz(localStorage.getItem('timezone') ?? 'Europe/Athens');
    return formatDateTime(dateTimeLocale.format());
  }

  function parseDate(date: string | null | undefined): string | null {
    if (!date) return null;
    if (!hasValidDateFormat(date, 'YYYY-MM-DD')) return date;
    let splitterChar = '-';
    let year = 0;
    let month = 1;
    let day = 2;
    switch (getDateFormat()) {
      case 'YYYY-MM-DD':
        break;
      case 'DD/MM/YYYY':
        splitterChar = '/';
        year = 2;
        month = 1;
        day = 0;
        break;
      case 'MM/DD/YYYY':
        splitterChar = '/';
        year = 2;
        month = 0;
        day = 1;
        break;
      default:
        break;
    }
    const dateArray = date.split(splitterChar);
    return `${dateArray[year]}-${dateArray[month].padStart(2, '0')}-${dateArray[
      day
    ].padStart(2, '0')}`;
  }

  function formatDateToServerSideFormat(
    originalDateString: string | null
  ): string | null {
    if (!originalDateString) return null;
    const originalFormat = getDateFormat();
    if (!hasValidDateFormat(originalDateString, originalFormat))
      return originalDateString;
    return moment(originalDateString, originalFormat).format('YYYY-MM-DD');
  }

  function timeAgo(timestamp: string): string {
    moment.locale(localStorage.getItem('locale') ?? 'el');
    return moment.utc(timestamp).fromNow();
  }

  function hasValidDateFormat(
    dateToCheck: string | null,
    dateFormat: string
  ): boolean {
    return moment(dateToCheck, dateFormat, true).isValid();
  }

  function today(): string {
    return formatDate(moment().format('YYYY-MM-DD'));
  }

  function getCurrentDate() {
    return moment().tz('Europe/Athens').format('YYYYMMDD/HHmmss');
  }

  return {
    getDateFormat,
    formatDate,
    formatDateTime,
    formatDateTimeLocale,
    parseDate,
    formatDateToServerSideFormat,
    timeAgo,
    today,
    getCurrentDate,
  };
}
