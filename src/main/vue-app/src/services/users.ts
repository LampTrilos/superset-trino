import axios from 'axios';
import { PageResponse, User } from './models';
import { Id } from 'src/services/types';

export const getUsers = async (query?: string) => {
  const response = await axios.get<PageResponse<User>>(
    '/api/v1/users?' + query ?? ''
  );
  return response.data;
};

export const getUser = async (id: Id) => {
  const response = await axios.get<User>('/api/v1/users/' + id);
  return response.data;
};

export const getMyUser = async () => {
  const response = await axios.get<User>('/api/v1/users/my');
  return response.data;
};

export const postUser = async (user: User) => {
  const response = await axios.post('/api/v1/users', user);
  return response.data;
};

export const deleteUser = async (id: Id) => {
  const response = await axios.post('/api/v1/users/' + id + '/delete');
  return response.data;
};

export const activateUser = async (id: Id) => {
  const response = await axios.post('/api/v1/users/' + id + '/activate');
  return response.data;
};

interface RoleVariables {
  userId: string;
  depUnit: string;
  ouId: string;
  role: string;
}

export const getUsersByDepUnitAndRole = async ({
  depUnit,
  role,
}: RoleVariables) => {
  const response = await axios.get<PageResponse<User>>(
    '/api/v1/users/duties?' +
      (depUnit != null ? 'depUnit=' + depUnit : '') +
      '&role=' +
      role
  );
  return response.data;
};

export const persistUserToDepUnitAndRole = async ({
  userId,
  depUnit,
  ouId,
  role,
}: RoleVariables) => {
  const response = await axios.post(
    '/api/v1/users/duties?userId=' +
      userId +
      (depUnit != null ? '&depUnit=' + depUnit : '') +
      (ouId != null ? '&ouId=' + ouId : '') +
      '&role=' +
      role
  );
  return response.data;
};

export const removeUserRoleFromDepUnit = async ({
  userId,
  depUnit,
  ouId,
  role,
}: RoleVariables) => {
  const response = await axios.post(
    '/api/v1/users/duties/delete?userId=' +
      userId +
      (depUnit != null ? '&depUnit=' + depUnit : '') +
      (ouId != null ? '&ouId=' + ouId : '') +
      '&role=' +
      role
  );
  return response.data;
};
