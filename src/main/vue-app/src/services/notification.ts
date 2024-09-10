import axios from 'axios';
import { GenAccountBalance, PageResponse } from 'src/services/models';
import { Id } from 'src/services/types';

export const getNotifications = async (size, index, read) => {
  const response = await axios.get<PageResponse<Notification>>(
    '/api/v1/notifications?' +
      (size != null ? 'size=' + size : '') +
      (index != null ? '&index=' + index : '') +
      (read != null ? '&read=' + read : '')
  );
  return response.data;
};

export const getNotificationByQuery = async (query?: string) => {
  const response = await axios.get<PageResponse<GenAccountBalance>>(
    '/api/v1/notifications?' + query ?? ''
  );
  return response.data;
};

export const markNotificationAsRead = async (id: Id) => {
  await axios.post('/api/v1/notifications/' + id + '/read');
};

export const markNotificationAsUnread = async (id: Id) => {
  await axios.post('/api/v1/notifications/' + id + '/unread');
};
