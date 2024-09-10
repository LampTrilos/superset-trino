import axios from 'axios';
import {
  CompoundTransaction,
  Enum,
  Item,
  PageResponse,
  TotalAccountBalance,
} from './models';
import { Id } from 'src/services/types';

/*interface ToOptionsItem {
  size?: number | null;
  index?: number | null;
  itemCode?: string | null;
  serialNumber?: string | null;
  depUnit?: string | null;
  assigneeUser?: string | null;
  assignedToDepUnit?: boolean | null;
  assignedToUser?: boolean | null;
}*/

export const getItems = async (
  size,
  index,
  itemCode,
  serialNumber,
  depUnit,
  assigneeUser,
  assignedToDepUnit,
  assignedToUser,
  includeChildrenUnits,
  myItems,
  assigneeOu,
  assignedToTopikosYpeythynos,
  manage,
  assignedToOu,
  inTransaction,
  temporalReserved,
  inTransactionOrTemporalReserved
) => {
  const response = await axios.get<PageResponse<Item>>(
    '/api/v1/items' +
      (myItems ? '/my?' : '?') +
      (size != null ? '&size=' + size : '') +
      (index != null ? '&index=' + index : '') +
      (itemCode != null ? '&itemCode=' + itemCode : '') +
      (serialNumber != null ? '&serialNumber=' + serialNumber : '') +
      (depUnit != null ? '&depUnit=' + depUnit : '') +
      (assigneeUser != null ? '&assigneeUser=' + assigneeUser : '') +
      (assignedToDepUnit != null
        ? '&assignedToDepUnit=' + assignedToDepUnit
        : '') +
      (assignedToUser != null ? '&assignedToUser=' + assignedToUser : '') +
      (includeChildrenUnits != null
        ? '&includeChildrenUnits=' + includeChildrenUnits
        : '') +
      (assigneeOu != null ? '&assigneeOu=' + assigneeOu : '') +
      (assignedToTopikosYpeythynos != null
        ? '&assignedToTopikosYpeythynos=' + assignedToTopikosYpeythynos
        : '') +
      (manage != null ? '&manage=' + manage : '') +
      (inTransaction != null ? '&inTransaction=' + inTransaction : '') +
      (temporalReserved != null
        ? '&temporalReserved=' + temporalReserved
        : '') +
      (inTransactionOrTemporalReserved != null
        ? '&inTransactionOrTemporalReserved=' + inTransactionOrTemporalReserved
        : '') +
      (assignedToOu != null ? '&assignedToOu=' + assignedToOu : '')
  );
  return response.data;
};

export const getItemsByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<TotalAccountBalance>>(
    '/api/v1/items?' + query ?? ''
  );
  return response.data;
};

export const getMyItemsByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<TotalAccountBalance>>(
    '/api/v1/items/my?' + query ?? ''
  );
  return response.data;
};

export const getItem = async (id: Id) => {
  const response = await axios.get<Item>('/api/v1/items/' + id);
  return response.data;
};

export const postItem = async (item: Item) => {
  const response = await axios.post('/api/v1/items', item);
  return response.data;
};

export const postItemMultiple = async (item: Item) => {
  const response = await axios.post('/api/v1/items/multiple', item);
  return response.data;
};

export const getItemLogs = async (apiPath, size, index, itemId: Id) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items/' +
      itemId +
      '/logs' + apiPath + '?' +
      (size != null ? '&size=' + size : '') +
      (index != null ? '&index=' + index : '')
  );
  return response.data;
};

export const hasPermissionsToUpdateItem = async (id: Id) => {
  if (id == undefined) {
    return true;
  }
  const response = await axios.get<boolean>(
    '/api/v1/items/has-update-permissions/' + id
  );
  return response.data;
};

export const getItemFiles = async (id: Id) => {
  const response = await axios.get<PageResponse<Item>>(
    '/api/v1/items/' + id + '/files'
  );
  return response.data;
};

export const downloadItemFile = async (
  id: Id,
  fileId: Id,
  fileName: string
) => {
  await axios({
    url: '/api/v1/items/' + id + '/files/' + fileId,
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

export const previewItemFile = async (id: Id, fileId: Id) => {
  await axios({
    url: '/api/v1/items/' + id + '/files/' + fileId,
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

interface ItemFileVariables {
  id: Id;
  fileId: Id;
}

export const deleteItemFile = async ({ id, fileId }: ItemFileVariables) => {
  await axios.post('/api/v1/items/' + id + '/files/' + fileId + '/delete');
};

export const getSyndesiKinitou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/items/syndesiKinitou?' + query ?? ''
  );
  return response.data;
};

export const getSyndesiKinitouById = async (id: Id) => {
  const response = await axios.get<Enum>('/api/v1/items/syndesiKinitou/' + id);
  return response.data;
};

export const deleteSyndesiKinitou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/items/syndesiKinitou/' + id + '/delete');
};

export const postSyndesiKinitou = async (syndesiKinitou: Enum) => {
  const response = await axios.post(
    '/api/v1/items/syndesiKinitou',
    syndesiKinitou
  );
  return response.data;
};

// Metaptosi
export const getMetaptosiSyndesiKinitou = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/syndesiKinitou?' +
      (size != null ? '&size=' + size : '') +
      (index != null ? '&index=' + index : '') +
      (search != null ? '&search=' + search : '') +
      (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiAvl = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/avl?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiAleksisfairo = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/aleksisfairo?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiGun= async (
  size,
  index,
  search: string,
  itemCode: number,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/gun?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (itemCode != null ? '&itemCode=' + itemCode : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiKeraia= async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/keraia?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiKinito= async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/kinito?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiKranosAntivalistiko= async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/kranosantivalistiko?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiAVlPda = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/avlpda?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};


export const getOchimaByRegNumSoap = async (registrationNumber: string) => {
  const response = await axios.get<Enum>('/api/v1/soapvehicle/findByRegNo?registrationNumber=' + registrationNumber);
  return response.data;
};

export const getMetaptosiPompodektis = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
  eidos: string,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/' + eidos + '?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiParelkomenaPompodekti = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/parelkomenoPompodekti?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

export const getMetaptosiMpatariaPompodekti = async (
  size,
  index,
  search: string,
  metaptothike: boolean,
) => {
  const response = await axios.get<PageResponse<CompoundTransaction>>(
    '/api/v1/items-metaptosi/mpatariaPompodekti?' +
    (size != null ? '&size=' + size : '') +
    (index != null ? '&index=' + index : '') +
    (search != null ? '&search=' + search : '') +
    (metaptothike != null ? '&metaptothike=' + metaptothike.value : '')
  );
  return response.data;
};

