import { useValidationRules } from 'src/utils/validation-rules';
import { describe, it, expect } from 'vitest';
import { withSetup } from '../../../test-utils';
import { installQuasarPlugin } from '@quasar/quasar-app-extension-testing-unit-vitest';

installQuasarPlugin();

describe('MyComponent', () => {
  it('increments the counter when button is clicked', async () => {
    const [result, app] = withSetup(() => useValidationRules());
    // mock provide for testing injections
    //app.provide(...)
    // run assertions
    const [rule] = result.requiredRule();
    expect(rule('hello')).toBe(true);
    expect(rule(42)).toBe(true);
    expect(rule(true)).toBe(true);
    expect(rule(0)).toBe(true);
    expect(rule(false)).toBe(true);
    expect(rule(true)).toBe(true);

    expect(rule('')).toBe('requiredField');
    expect(rule(null)).toBe('requiredField');
    expect(rule(undefined)).toBe('requiredField');
    // trigger onUnmounted hook if needed
    app.unmount();
  });
});
//https://vuejs.org/guide/scaling-up/testing.html#recipes
