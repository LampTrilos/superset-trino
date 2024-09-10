// This file will be run before each test file
import { useI18n } from 'vue-i18n';
import { vi } from 'vitest';

vi.mock('vue-i18n');

useI18n.mockReturnValue({
  t: (tKey) => tKey,
});
