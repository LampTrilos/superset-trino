import { boot } from 'quasar/wrappers';
import { useBreadcrumbsStore } from 'stores/breadcrumbs';
import Util from 'src/utils/util';

export default boot(({ router, store }) => {
  router.beforeEach((to, from, next) => {
    // console.debug('Is user authenticated: ' + Util.methods.loggedIn());
    // console.debug('Path: ' + to.fullPath);
    // console.debug('Page requires auth: ' + to.meta.requiresAuth);
    if (to.meta.requiresAuth && !Util.methods.loggedIn()) {
      next({ name: 'Home' });
    } else {
      const currentPathName = to.name;
      // Check if the current route is stateful and requires redirection based on stored query parameters
      if (
        to.meta.stateful &&
        to.name !== from.name &&
        sessionStorage[currentPathName] &&
        sessionStorage[currentPathName] !== JSON.stringify(to.query)
      ) {
        // Stored query parameters exist and are different from the current route's query parameters
        //console.log(
        //   'Redirecting from session storage. Path:',
        //   sessionStorage[currentPathName]
        // );

        // Retrieve and parse the stored query parameters
        const storedQueryParams = JSON.parse(sessionStorage[to.name]);

        // Create a new navigation object with the stored query parameters
        const navigationObject = {
          name: to.name,
          query: storedQueryParams,
        };

        // Perform the navigation with the stored query parameters
        next(navigationObject);
      } else {
        next();
      }
    }
  });

  router.beforeResolve((to) => {
    const breadcrumbsStore = useBreadcrumbsStore(store);
    breadcrumbsStore.$patch({
      breadcrumbs: to.meta.breadcrumbs,
    });
  });

  router.afterEach((to) => {
    // Check if the route is stateful and requires saving query parameters
    if (to.meta?.stateful) {
      // Convert the query parameters object to a JSON string
      const queryParamsString = JSON.stringify(to.query);
      // Save the JSON string in the session storage with route name as the key
      sessionStorage.setItem(to.name, queryParamsString);
    }
  });
});
