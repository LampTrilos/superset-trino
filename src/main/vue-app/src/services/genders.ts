import axios from 'axios';
import { Gender, PageResponse } from './models';

export const getGenders = async () => {
  const response = await axios.get<PageResponse<Gender>>('/api/v1/genders');
  return response.data;
};
