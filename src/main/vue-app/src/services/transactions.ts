import axios from 'axios';
import {
  CompoundTransaction,
  ListResponse,
  PageResponse, SealingApplication, SignRequest,
  TransactionType,
} from './models';
import { Id } from 'src/services/types';

export const getTransactionTypes = async () => {
  const response = await axios.get<PageResponse<TransactionType>>(
    '/api/v1/transactions-types'
  );
  return response.data;
};

export const getCompoundTransactions = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions?status=COMPLETED&' + query ?? ''
  );
  return response.data;
};

export const getCompoundTransactionsDraft = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions?status=DRAFT&' + query ?? ''
  );
  return response.data;
};

export const getCompoundTransactionsPending = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions?pending=true&' + query ?? ''
  );
  return response.data;
};

export const getCompoundTransactionsForSign = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions?status=FOR_SIGNATURE&' + query ?? ''
  );
  return response.data;
};

export const getCompoundTransactionsRejected = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions?status=REJECTED&' + query ?? ''
  );
  return response.data;
};

export const getMyCompoundTransactionsCompleted = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/my?status=COMPLETED&' + query ?? ''
  );
  return response.data;
};

export const getMyCompoundTransactionsForSign = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/my?status=FOR_SIGNATURE&' + query ?? ''
  );
  return response.data;
};

export const getMyCompoundTransactionsRejected = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/my?status=REJECTED&' + query ?? ''
  );
  return response.data;
};

export const getMyCompoundTransactionsDraft = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/my?status=DRAFT&' + query ?? ''
  );
  return response.data;
};

export const getMyCompoundTransactionsPending = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/my?pending=true&' + query ?? ''
  );
  return response.data;
};

export const getCompoundTransaction = async (id: Id) => {
  const response = await axios.get<CompoundTransaction>(
    '/api/v1/transactions/' + id
  );
  return response.data;
};

export const getLogsTransactions = async (size, index, transactionId: Id) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/' +
      transactionId +
      '/logs?' +
      (size != null ? '&size=' + size : '') +
      (index != null ? '&index=' + index : '')
  );
  return response.data;
};

export const postCompoundTransaction = async (
  compoundTransaction: CompoundTransaction
) => {
  const response = await axios.post(
    '/api/v1/transactions',
    compoundTransaction
  );
  return response.data;
};

export const sendCompoundTransactionForSign = async (id: Id) => {
  await axios.post('/api/v1/transactions/send-for-sign/' + id);
};

export const rejectCompoundTransaction = async (id: Id) => {
  await axios.post('/api/v1/transactions/reject/' + id);
};

export const resendNotificationAndEmailCompoundTransaction = async (id: Id) => {
  await axios.post('/api/v1/transactions/resend-email/' + id);
};

interface SignVariables {
  id: Id;
  verifyCode: string;
}

export const signCompoundTransaction = async ({
  id,
  verifyCode,
}: SignVariables) => {
  await axios.post(
    '/api/v1/transactions/sign/' + id + '?verifyCode=' + verifyCode
  );
};

export const hasPermissionsToSignCompoundTransaction = async (id: Id) => {
  const response = await axios.get<boolean>(
    '/api/v1/transactions/has-sign-permissions/' + id
  );
  return response.data;
};

export const hasPermissionsToRejectCompoundTransaction = async (id: Id) => {
  const response = await axios.get<boolean>(
    '/api/v1/transactions/has-reject-permissions/' + id
  );
  return response.data;
};

export const deleteCompoundTransaction = async (id: Id) => {
  await axios.post('/api/v1/transactions/' + id + '/delete');
};

export const getITransactionsByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/itransactions?' + query ?? ''
  );
  return response.data;
};

export const getMyITransactionsByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/itransactions/my?' + query ?? ''
  );
  return response.data;
};

export const getITransactionsByItemId = async (size, index, itemId: Id) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/transactions/itransactions?' +
      (size != null ? '&size=' + size : '') +
      (index != null ? '&index=' + index : '') +
      (itemId != null ? '&itemId=' + itemId : '')
  );
  return response.data;
};

export const getCompoundTransactionFiles = async (id: Id) => {
  const response = await axios.get<ListResponse<CompoundTransaction>>(
    '/api/v1/transactions/' + id + '/files'
  );
  return response.data;
};

export const getCompoundTransactionFilesForSign = async (id: Id) => {
  const response = await axios.get<ListResponse<CompoundTransaction>>(
    '/api/v1/transactions/' + id + '/files/for-sign'
  );
  return response.data;
};

export const downloadCompoundTransactionFile = async (
  id: Id,
  fileId: Id,
  fileName: string
) => {
  await axios({
    url: '/api/v1/transactions/files/' + fileId,
    method: 'GET',
    responseType: 'blob', // important
  }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
  });
};

export const previewCompoundTransactionFile = async (id: Id, fileId: Id) => {
  await axios({
    url: '/api/v1/transactions/files/' + fileId,
    method: 'GET',
    responseType: 'blob', // important
  }).then((response) => {
    const file = new Blob([response.data], {
      type: response.headers['content-type'],
    });
    const fileURL = URL.createObjectURL(file);
    window.open(fileURL, '_blank');
  });
};

interface CompoundTransactionFileVariables {
  id: Id;
  fileId: Id;
}

export const deleteCompoundTransactionFile = async ({
  id,
  fileId,
}: CompoundTransactionFileVariables) => {
  await axios.post(
    '/api/v1/transactions/' + id + '/files/' + fileId + '/delete'
  );
};

export const getChildrenCompoundTransaction = async (id: Id) => {
  const response = await axios.get<ListResponse<CompoundTransaction>>(
    '/api/v1/transactions/' + id + '/children'
  );
  return response.data;
};

export const downloadPrototypeFile = async () => {
  await axios({
    url: '/api/v1/transactions/prototype-file',
    method: 'GET',
    responseType: 'blob', // important
  }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'Apografi2023.xlsx');
    document.body.appendChild(link);
    link.click();
  });
};

//---------------------Pol Seal Start---------------------//
//Fetches the Sealing Applications
export const getSealingApplications = async (size: number, index: number) => {
  const response = await axios.get<PageResponse<SealingApplication>>(
    '/api/v1/sealing-applications/getSealingApps?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : ''));
  return response.data;
};

//Fetches a Sealing App by Id
export const getSealingAppById = async (id: number) => {
  const response = await axios.get<PageResponse<SealingApplication>>(
    '/api/v1/sealing-applications/getSealingAppById/' + id);
  return response.data;
};

//Creates or Edits a Sealing Application
export const insertOrUpdateSealingApp = async ( editedNewApplication : SealingApplication) => {
  //console.log(editedNewApplication)
  await axios.post(
    '/api/v1/sealing-applications/insertOrUpdateSealingApp', editedNewApplication
  );
};

//Deletes Sealing Applications
export const deleteSealingApplications = async (listOfApplicationsIds: string[]) => {
  const response = await axios.post(
    '/api/v1/sealing-applications/deleteApps', listOfApplicationsIds)
  return response;
};

//Sends an OTP authentication via the OTP page
export const validateOTPviaOTPPage = async ( signRequest : SignRequest, callbackUrl: string) => {
  //console.log(callbackUrl)
  //console.log(editedNewApplication)
  await axios.post(
    '/api/v1/signRequest/newSignRequest', signRequest
    //'/apiasdasd/sdfsadfasdf', signRequest
  ).then(() => {
    console.log('Callback happening..')
    console.log(callbackUrl)
    axios.get(
      callbackUrl
    )
  })
};

//---------------------Pol Seal End ---------------------//
