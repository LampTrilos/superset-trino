import axios from 'axios';
import {Announcement} from 'src/services/models';

export const getAnnouncement = async () => {
  const response = await axios.get<Announcement>(
    '/api/v1/announcements?'
  );
  return response.data;
};


export const postAnnouncement = async (announcement: Announcement) => {
  const response = await axios.post('/api/v1/announcements', announcement);
  return response.data;
};
