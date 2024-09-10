const ENV = require('dotenv').config().parsed;
/*
  Use an environment variable set in package.json scripts to determine
  the applications runtime environment. Add more switch cases as
  need for additional environments.
*/

module.exports = (QENV) => {
  if (!['DEV', 'STAGE', 'PROD'].includes(QENV)) {
    throw Error('Unknown or not supplied environment variable');
  }
  return {
    PUBLIC_PATH: ENV[`${QENV}_PUBLIC_PATH`],
  };
};
