import axios from 'axios';
import { GenDepUnit, PageResponse, ParDepUnit } from './models';
import { Id } from 'src/services/types';

export const getGenDepUnits = async (query?: string) => {
  const response = await axios.get<PageResponse<GenDepUnit>>(
    '/api/v1/departments/generals?' + query ?? ''
  );
  return response.data;
};

export const manageGenDepUnits = async (query?: string) => {
  const manageQuery = query ? `${query}&manage=true` : 'manage=true';
  return getGenDepUnits(manageQuery);
};

export const getGenDepUnit = async (id: Id) => {
  const response = await axios.get<GenDepUnit>(
    '/api/v1/departments/generals/' + id
  );
  return response.data;
};

export const deleteGenDepUnit = async (id: Id) => {
  await axios.post<GenDepUnit>(
    '/api/v1/departments/generals/' + id + '/delete'
  );
};

export const postGenDepUnit = async (genDepUnit: GenDepUnit) => {
  const response = await axios.post('/api/v1/departments/generals', genDepUnit);
  return response.data;
};

export const getParDepUnits = async (query?: string) => {
  const response = await axios.get<PageResponse<ParDepUnit>>(
    '/api/v1/departments/partials?' + query ?? ''
  );
  return response.data;
};

export const manageParDepUnits = async (query?: string) => {
  const manageQuery = query ? `${query}&manage=true` : 'manage=true';
  return getParDepUnits(manageQuery);
};

export const getParDepUnit = async (id: Id) => {
  const response = await axios.get<ParDepUnit>(
    '/api/v1/departments/partials/' + id
  );
  return response.data;
};

export const deleteParDepUnit = async (id: Id) => {
  await axios.post<GenDepUnit>(
    '/api/v1/departments/partials/' + id + '/delete'
  );
};

export const postParDepUnit = async (parDepUnit: ParDepUnit) => {
  const response = await axios.post('/api/v1/departments/partials', parDepUnit);
  return response.data;
};
