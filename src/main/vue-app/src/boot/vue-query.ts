import { boot } from 'quasar/wrappers';
import {
  QueryClient,
  VueQueryPlugin,
  VueQueryPluginOptions,
} from '@tanstack/vue-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 0,
      refetchOnWindowFocus: false,
    },
  },
});

const vueQueryPluginOptions: VueQueryPluginOptions = {
  queryClient,
};

export default boot(({ app }) => {
  app.use(VueQueryPlugin, vueQueryPluginOptions);
});
