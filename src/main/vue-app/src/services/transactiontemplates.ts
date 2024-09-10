import axios from 'axios';
import { TransactionTemplate, PageResponse } from './models';
import { Id } from 'src/services/types';

export const getTransactionTemplates = async (query?: string) => {
  const response = await axios.get<PageResponse<TransactionTemplate>>(
    '/api/v1/transaction-templates?' + query ?? ''
  );
  return response.data;
};

export const getTransactionTemplate = async (id: Id) => {
  const response = await axios.get<TransactionTemplate>(
    '/api/v1/transaction-templates/' + id
  );
  return response.data;
};

export const deleteTransactionTemplate = async (id: Id) => {
  await axios.post<TransactionTemplate>(
    '/api/v1/transaction-templates/' + id + '/delete'
  );
};

export const postTransactionTemplate = async (
  transactionTemplate: TransactionTemplate
) => {
  const response = await axios.post(
    '/api/v1/transaction-templates',
    transactionTemplate
  );
  return response.data;
};
