import axios from 'axios';
import { Enum, PageResponse } from './models';
import { Id } from 'src/services/types';

export const getDiametrimaOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/diametrimaOplou?' + query ?? ''
  );
  return response.data;
};

export const getManageDiametrimaOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/diametrimaOplou?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getDiametrimaOplouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/diametrimaOplou/' + id
  );
  return response.data;
};

export const deleteDiametrimaOplou = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/diametrimaOplou/' + id + '/delete'
  );
};

export const postDiametrimaOplou = async (diametrimaOplou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/diametrimaOplou',
    diametrimaOplou
  );
  return response.data;
};

export const getEidosOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosOplou?' + query ?? ''
  );
  return response.data;
};

export const getEidosOplouById = async (id: Id) => {
  const response = await axios.get<Enum>('/api/v1/item-enums/eidosOplou/' + id);
  return response.data;
};

export const deleteEidosOplou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/eidosOplou/' + id + '/delete');
};

export const postEidosOplou = async (eidosOplou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/eidosOplou',
    eidosOplou
  );
  return response.data;
};

export const getManageMarkaOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/markaOplou?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getMarkaOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/markaOplou?' + query ?? ''
  );
  return response.data;
};

export const getMarkaOplouById = async (id: Id) => {
  const response = await axios.get<Enum>('/api/v1/item-enums/markaOplou/' + id);
  return response.data;
};

export const deleteMarkaOplou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/markaOplou/' + id + '/delete');
};

export const postMarkaOplou = async (markaOplou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/markaOplou',
    markaOplou
  );
  return response.data;
};

export const getModeloOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloOplou?' + query ?? ''
  );
  return response.data;
};

export const getManageModeloOplou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloOplou?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getModeloOplouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloOplou/' + id
  );
  return response.data;
};

export const deleteModeloOplou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/modeloOplou/' + id + '/delete');
};

export const postModeloOplou = async (modeloOplou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/modeloOplou',
    modeloOplou
  );
  return response.data;
};

export const getChora = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/chora?' + query ?? ''
  );
  return response.data;
};

export const getChoraById = async (id: Id) => {
  const response = await axios.get<Enum>('/api/v1/item-enums/chora/' + id);
  return response.data;
};

export const deleteChora = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/chora/' + id + '/delete');
};

export const postChora = async (chora: Enum) => {
  const response = await axios.post('/api/v1/item-enums/chora', chora);
  return response.data;
};

export const getSychnotitaKeraias = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/sychnotitaKeraias?' + query ?? ''
  );
  return response.data;
};

export const getSychnotitaKeraiasById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/sychnotitaKeraias/' + id
  );
  return response.data;
};

export const deleteSychnotitaKeraias = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/sychnotitaKeraias/' + id + '/delete'
  );
};

export const postSychnotitaKeraias = async (sychnotitaKeraias: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/sychnotitaKeraias',
    sychnotitaKeraias
  );
  return response.data;
};

export const getMpantaKeraias = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/mpantaKeraias?' + query ?? ''
  );
  return response.data;
};

export const getMpantaKeraiasById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/mpantaKeraias/' + id
  );
  return response.data;
};

export const deleteMpantaKeraias = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/mpantaKeraias/' + id + '/delete');
};

export const postMpantaKeraias = async (mpantaKeraias: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/mpantaKeraias',
    mpantaKeraias
  );
  return response.data;
};

export const getMarkaKinitou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/markaKinitou?' + query ?? ''
  );
  return response.data;
};

export const getMarkaKinitouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/markaKinitou/' + id
  );
  return response.data;
};

export const deleteMarkaKinitou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/markaKinitou/' + id + '/delete');
};

export const postMarkaKinitou = async (markaKinitou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/markaKinitou',
    markaKinitou
  );
  return response.data;
};

export const getModeloKinitou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloKinitou?' + query ?? ''
  );
  return response.data;
};

export const getModeloKinitouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloKinitou/' + id
  );
  return response.data;
};

export const deleteModeloKinitou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/modeloKinitou/' + id + '/delete');
};

export const postModeloKinitou = async (modeloKinitou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/modeloKinitou',
    modeloKinitou
  );
  return response.data;
};

export const getEidosPompodekti = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosPompodekti?' + query ?? ''
  );
  return response.data;
};

export const getEidosPompodektiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/eidosPompodekti/' + id
  );
  return response.data;
};

export const deleteEidosPompodekti = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/eidosPompodekti/' + id + '/delete'
  );
};

export const postEidosPompodekti = async (eidosPompodekti: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/eidosPompodekti',
    eidosPompodekti
  );
  return response.data;
};

export const getModeloAleksisfairou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloAleksisfairou?' + query ?? ''
  );
  return response.data;
};

export const getManageModeloAleksisfairou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloAleksisfairou?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getModeloAleksisfairouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloAleksisfairou/' + id
  );
  return response.data;
};

export const deleteModeloAleksisfairou = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/modeloAleksisfairou/' + id + '/delete'
  );
};

export const postModeloAleksisfairou = async (modeloAleksisfairou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/modeloAleksisfairou',
    modeloAleksisfairou
  );
  return response.data;
};

export const getModeloKranousAntivalistikou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloKranousAntivalistikou?' + query ?? ''
  );
  return response.data;
};

export const getManageModeloKranousAntivalistikou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloKranousAntivalistikou?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getModeloKranousAntivalistikouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloKranousAntivalistikou/' + id
  );
  return response.data;
};

export const deleteModeloKranousAntivalistikou = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/modeloKranousAntivalistikou/' + id + '/delete'
  );
};

export const postModeloKranousAntivalistikou = async (
  modeloKranousAntivalistikou: Enum
) => {
  const response = await axios.post(
    '/api/v1/item-enums/modeloKranousAntivalistikou',
    modeloKranousAntivalistikou
  );
  return response.data;
};

export const getModeloPlakasAntivalistikis = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloPlakasAntivalistikis?' + query ?? ''
  );
  return response.data;
};

export const getManageModeloPlakasAntivalistikis = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloPlakasAntivalistikis?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getModeloPlakasAntivalistikisById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloPlakasAntivalistikis/' + id
  );
  return response.data;
};

export const deleteModeloPlakasAntivalistikis = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/modeloPlakasAntivalistikis/' + id + '/delete'
  );
};

export const postModeloPlakasAntivalistikis = async (
  modeloPlakasAntivalistikis: Enum
) => {
  const response = await axios.post(
    '/api/v1/item-enums/modeloPlakasAntivalistikis',
    modeloPlakasAntivalistikis
  );
  return response.data;
};

export const getYpokatastasiPompodekti = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/ypokatastasiPompodekti?' + query ?? ''
  );
  return response.data;
};

export const getYpokatastasiPompodektiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/ypokatastasiPompodekti/' + id
  );
  return response.data;
};

export const deleteYpokatastasiPompodekti = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/ypokatastasiPompodekti/' + id + '/delete'
  );
};

export const postYpokatastasiPompodekti = async (
  ypokatastasiPompodekti: Enum
) => {
  const response = await axios.post(
    '/api/v1/item-enums/ypokatastasiPompodekti',
    ypokatastasiPompodekti
  );
  return response.data;
};

export const getMarkaPompodekti = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/markaPompodekti?' + query ?? ''
  );
  return response.data;
};

export const getMarkaPompodektiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/markaPompodekti/' + id
  );
  return response.data;
};

export const deleteMarkaPompodekti = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/markaPompodekti/' + id + '/delete'
  );
};

export const postMarkaPompodekti = async (markaPompodekti: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/markaPompodekti',
    markaPompodekti
  );
  return response.data;
};

export const getModeloPompodekti = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloPompodekti?' + query ?? ''
  );
  return response.data;
};

export const getModeloPompodektiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloPompodekti/' + id
  );
  return response.data;
};

export const deleteModeloPompodekti = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/modeloPompodekti/' + id + '/delete'
  );
};

export const postModeloPompodekti = async (modeloPompodekti: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/modeloPompodekti',
    modeloPompodekti
  );
  return response.data;
};

export const getKatigoriaAnalogikouPompodekti = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/katigoriaAnalogikouPompodekti?' + query ?? ''
  );
  return response.data;
};

export const getKatigoriaAnalogikouPompodektiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/katigoriaAnalogikouPompodekti/' + id
  );
  return response.data;
};

export const deleteKatigoriaAnalogikouPompodekti = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/katigoriaAnalogikouPompodekti/' + id + '/delete'
  );
};

export const postKatigoriaAnalogikouPompodekti = async (
  katigoriaAnalogikouPompodekti: Enum
) => {
  const response = await axios.post(
    '/api/v1/item-enums/katigoriaAnalogikouPompodekti',
    katigoriaAnalogikouPompodekti
  );
  return response.data;
};

export const getEidosParelkomenou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosParelkomenou?' + query ?? ''
  );
  return response.data;
};

export const getManageEidosParelkomenou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosParelkomenou?manage=true&' + query ?? ''
  );
  return response.data;
};

export const getEidosParelkomenouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/eidosParelkomenou/' + id
  );
  return response.data;
};

export const deleteEidosParelkomenou = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/eidosParelkomenou/' + id + '/delete'
  );
};

export const postEidosParelkomenou = async (eidosParelkomenou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/eidosParelkomenou',
    eidosParelkomenou
  );
  return response.data;
};

export const getEidosSyndesisKinitou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosSyndesisKinitou?' + query ?? ''
  );
  return response.data;
};

export const getEidosSyndesisKinitouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/eidosSyndesisKinitou/' + id
  );
  return response.data;
};

export const deleteEidosSyndesisKinitou = async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/eidosSyndesisKinitou/' + id + '/delete'
  );
};

export const postEidosSyndesisKinitou = async (eidosSyndesisKinitou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/eidosSyndesisKinitou',
    eidosSyndesisKinitou
  );
  return response.data;
};

export const getEidosDiktyou = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosDiktyou?' + query ?? ''
  );
  return response.data;
};

export const getEidosDiktyouById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/eidosDiktyou/' + id
  );
  return response.data;
};

export const deleteEidosDiktyou = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/eidosDiktyou/' + id + '/delete');
};

export const postEidosDiktyou = async (eidosDiktyou: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/eidosDiktyou',
    eidosDiktyou
  );
  return response.data;
};

export const getEidosThesis = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/eidosThesis?' + query ?? ''
  );
  return response.data;
};

export const getEidosThesisById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/eidosThesis/' + id
  );
  return response.data;
};

export const deleteEidosThesis = async (id: Id) => {
  await axios.post<Enum>('/api/v1/item-enums/eidosThesis/' + id + '/delete');
};

export const postEidosThesis = async (eidosThesis: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/eidosThesis',
    eidosThesis
  );
  return response.data;
};

export const getManageOu = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/ous?' + query ?? ''
  );
  return response.data;
};

export const getOuById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/ous/' + id
  );
  return response.data;
};

export const getManageVendorPliroforiki = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/vendorPliroforiki?' + query ?? ''
  );
  return response.data;
};

export const getVendorPliroforikiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/vendorPliroforiki/' + id
  );
  return response.data;
};

export const getManageModeloPliroforiki = async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/modeloPliroforiki?' + query ?? ''
  );
  return response.data;
};

export const getModeloPliroforikiById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/modeloPliroforiki/' + id
  );
  return response.data;
};

export const getRouxismosMegethos= async (query?: string) => {
  const response = await axios.get<PageResponse<Enum>>(
    '/api/v1/item-enums/rouxismosMegethos?' + query ?? ''
  );
  return response.data;
};

export const getRouxismosMegethosById = async (id: Id) => {
  const response = await axios.get<Enum>(
    '/api/v1/item-enums/rouxismosMegethos/' + id
  );
  return response.data;
};

export const deleteRouxismosMegethos= async (id: Id) => {
  await axios.post<Enum>(
    '/api/v1/item-enums/rouxismosMegethos/' + id + '/delete'
  );
};

export const postRouxismosMegethos= async (rouxismosMegethos: Enum) => {
  const response = await axios.post(
    '/api/v1/item-enums/rouxismosMegethos',
    rouxismosMegethos
  );
  return response.data;
};

