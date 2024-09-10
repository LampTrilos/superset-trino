import axios from 'axios';
import { useAccountStore } from 'stores/account';

const accountStore = useAccountStore();

export const getMyPermissions = async () => {
  const response = await axios.get('/api/v1/permissions/my');
  accountStore.$patch({
    permissions: response.data,
  });
  return response.data;
};

export const getMyDepartments = async () => {
  const response = await axios.get('/api/v1/permissions/my-departments');
  return response.data;
};
