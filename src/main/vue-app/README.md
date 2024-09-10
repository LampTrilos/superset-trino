# Pol erp (ubitech)

A starting project for ubitech vue 3 front end.
The project uses [Quasar Framework](https://quasar.dev/)

## Authentication

Keycloak is used. Please check the corresponding keycloak version in 'package.json'

```bash
cp .env.example .env
```

## Install the dependencies

```bash
npm install
```

### Start the app in development mode (hot-code reloading, error reporting, etc.)

```bash
npm run dev
```

### Lint the files

```bash
npm run lint
```

### Format the files

```bash
npm run format
```

### Build the app for production

```bash
npm run build
```

### Run integration tests

```bash
npx cypress open
```

### Customize the configuration

See [Configuring quasar.config.js](https://v2.quasar.dev/quasar-cli-vite/quasar-config-js).

### Change language

Change language in `quasar.config.js` , `i18n/index.ts` and momentjs `date-formatter.ts`
