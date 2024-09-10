import { defineStore } from 'pinia';

export const useBreadcrumbsStore = defineStore('breadcrumbs', {
  state: () => ({
    breadcrumbs: [],
  }),
  actions: {
    setDynamicPropertyToLastBreadcrumb(value: string) {
      const lastElement = this.breadcrumbs.pop();
      if (lastElement == undefined) {
        return;
      }
      lastElement.label = value;
      this.breadcrumbs.push(lastElement);
    },
  },
});
