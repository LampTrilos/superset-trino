import { copyToClipboard as copyToClipboardQ, Notify } from 'quasar';

export function copyToClipboard(text: string): void {
  copyToClipboardQ(text)
    .then(() => {
      Notify.create({
        message: 'Το κείμενο αντιγράφηκε',
        color: 'accent',
      });
    })
    .catch(() => {
      Notify.create({
        message: 'Σφάλμα αντιγραφής',
        color: 'negative',
      });
    });
}
