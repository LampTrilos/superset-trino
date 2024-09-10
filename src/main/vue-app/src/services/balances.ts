import axios from 'axios';
import {
  GenAccountBalance,
  Item,
  OuAccountBalance,
  PageResponse,
  ParAccountBalance,
  UserAccountBalance,
} from './models';
import { Id } from 'src/services/types';

export const getAccountBalances = async (
  size,
  index,
  itemCode,
  type,
  depUnit,
  user,
  ou,
  assignedToTopikosYpeythynos
) => {
  const response = await axios.get<PageResponse<Item>>(
    '/api/v1/balances/' +
      type +
      '?' +
      (size != null ? '&size=' + size : '') +
      (index != null ? '&index=' + index : '') +
      (itemCode != null ? '&itemCode=' + itemCode : '') +
      (depUnit != null
        ? (type == 'partials' ? '&genDepUnit=' : '&depUnit=') + depUnit
        : '') +
      (user != null ? '&user=' + user : '') +
      (ou != null ? '&ou=' + ou : '') +
      (assignedToTopikosYpeythynos != null
        ? '&assignedToTopikosYpeythynos=' + assignedToTopikosYpeythynos
        : '')
  );
  return response.data;
};

export const getAccountBalancesByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<GenAccountBalance>>(
    '/api/v1/balances?' + query ?? ''
  );
  return response.data;
};

export const getGenAccountBalances = async (query?: string) => {
  const response = await axios.get<PageResponse<GenAccountBalance>>(
    '/api/v1/balances/generals?' + query ?? ''
  );
  return response.data;
};

export const getGenAccountBalance = async (id: Id) => {
  const response = await axios.get<GenAccountBalance>(
    '/api/v1/balances/generals/' + id
  );
  return response.data;
};

export const getParAccountBalances = async (query?: string) => {
  const response = await axios.get<PageResponse<ParAccountBalance>>(
    '/api/v1/balances/partials?' + query ?? ''
  );
  return response.data;
};

export const getParAccountBalance = async (id: Id) => {
  const response = await axios.get<ParAccountBalance>(
    '/api/v1/balances/partials/' + id
  );
  return response.data;
};

export const getOuAccountBalances = async (query?: string) => {
  const response = await axios.get<PageResponse<OuAccountBalance>>(
    '/api/v1/balances/ous?' + query ?? ''
  );
  return response.data;
};

export const getOuAccountBalance = async (id: Id) => {
  const response = await axios.get<OuAccountBalance>(
    '/api/v1/balances/ous/' + id
  );
  return response.data;
};

export const getMyAccountBalances = async (query?: string) => {
  const response = await axios.get<PageResponse<UserAccountBalance>>(
    '/api/v1/balances/my?' + query ?? ''
  );
  return response.data;
};

export const getInventoryAccountBalances = async (query?: string) => {
  const response = await axios.get<PageResponse<GenAccountBalance>>(
    '/api/v1/balances/inventory?' + query ?? ''
  );
  return response.data;
};
