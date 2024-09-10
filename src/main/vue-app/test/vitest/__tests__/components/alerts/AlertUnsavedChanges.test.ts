import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import { Quasar } from 'quasar';
import { createI18n } from 'vue-i18n';

import AlertUnsavedChanges from 'components/alerts/AlertUnsavedChanges.vue';
import { installQuasarPlugin } from '@quasar/quasar-app-extension-testing-unit-vitest';

installQuasarPlugin();

/*const wrapperFactory = () => mount(AlertUnsavedChanges, {
  global: {
    plugins: [Quasar],
    mocks: { // Here the solution
      $t: (msg) => msg
    }
  }
})*/

//const wrapper = wrapperFactory();

describe('Discord integration form', () => {
  it('mount component', async () => {
    const i18n = createI18n({
      messages: {},
      legacy: false,
    });

    const wrapper = mount(AlertUnsavedChanges, {
      global: {
        plugins: [i18n],
      },
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
