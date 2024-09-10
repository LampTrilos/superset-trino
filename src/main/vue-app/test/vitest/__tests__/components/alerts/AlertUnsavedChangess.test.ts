import { describe, it, expect, vi } from 'vitest';
import { mount, config } from '@vue/test-utils';
import AlertUnsavedChanges from 'components/alerts/AlertUnsavedChanges.vue';
import { installQuasarPlugin } from '@quasar/quasar-app-extension-testing-unit-vitest';

installQuasarPlugin();

describe('HelloWorld', () => {
  it('renders properly', async () => {
    const wrapper = mount(AlertUnsavedChanges, {
      props: {
        objectToWatch: {
          firstName: 'Niki',
        },
      },
    });
    expect(wrapper.html()).not.contain('unsavedChanges');
    await wrapper.setProps({
      objectToWatch: {
        firstName: 'Niki!',
      },
    });
    expect(wrapper.html()).contain('unsavedChanges');
  });
});
