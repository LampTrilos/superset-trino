import axios from 'axios';
import {
  GenAccountBalance,
  ItemCode,
  ItemCodeCategory,
  ItemCodeTemplate,
  ItemCodeTicket,
  ListResponse,
  PageResponse,
} from './models';
import { Id } from 'src/services/types';

export const getItemCodeCategories = async (query?: string) => {
  const response = await axios.get<PageResponse<ItemCodeCategory>>(
    '/api/v1/codes/categories?' + query ?? ''
  );
  return response.data;
};

export const getItemCodeCategory = async (id: Id) => {
  const response = await axios.get<ItemCodeCategory>(
    '/api/v1/codes/categories/' + id
  );
  return response.data;
};

export const postItemCodeCategory = async (
  itemCodeCategory: ItemCodeCategory
) => {
  const response = await axios.post(
    '/api/v1/codes/categories',
    itemCodeCategory
  );
  return response.data;
};

export const deleteItemCodeCategory = async (id: Id) => {
  await axios.post<ItemCodeCategory>(
    '/api/v1/codes/categories/' + id + '/delete'
  );
};

export const getItemCodes = async (query?: string) => {
  const response = await axios.get<PageResponse<ItemCode>>(
    '/api/v1/codes?' + query ?? ''
  );
  return response.data;
};

export const getItemCode = async (id: Id) => {
  const response = await axios.get<ItemCode>('/api/v1/codes/' + id);
  return response.data;
};

export const postItemCode = async (itemCode: ItemCode) => {
  const response = await axios.post('/api/v1/codes', itemCode);
  return response.data;
};

export const deleteItemCode = async (id: Id) => {
  await axios.post<ItemCodeCategory>('/api/v1/codes/' + id + '/delete');
};

export const getItemCodeTemplates = async () => {
  const response = await axios.get<ListResponse<ItemCodeTemplate>>(
    '/api/v1/item-code-templates'
  );
  return response.data;
};

export const getItemCodeTicketByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<GenAccountBalance>>(
    '/api/v1/codes/tickets?' + query ?? ''
  );
  return response.data;
};

export const getMyItemCodeTicketByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<GenAccountBalance>>(
    '/api/v1/codes/tickets/my?' + query ?? ''
  );
  return response.data;
};

export const postItemCodeTicket = async (itemCodeTicket: ItemCodeTicket) => {
  const response = await axios.post('/api/v1/codes/tickets', itemCodeTicket);
  return response.data;
};

export const markItemCodeTicketAsRead = async (id: Id) => {
  await axios.post('/api/v1/codes/tickets/' + id + '/read');
};

export const markItemCodeTicketAsUnread = async (id: Id) => {
  await axios.post('/api/v1/codes/tickets/' + id + '/unread');
};
