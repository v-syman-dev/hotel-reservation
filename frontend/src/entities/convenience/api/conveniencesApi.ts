import { http } from '@/shared/api/http';
import type { Convenience } from '@/entities/convenience/model/types';

export const conveniencesApi = {
  list: async () => {
    const response = await http.get<Convenience[]>('/conveniences');
    return response.data;
  },

  create: async (payload: Convenience) => {
    const response = await http.post<Convenience>('/conveniences', payload);
    return response.data;
  },

  update: async (payload: Convenience) => {
    const response = await http.put<Convenience>('/conveniences', payload);
    return response.data;
  },

  remove: async (convenienceId: number) => {
    await http.delete(`/conveniences/${convenienceId}`);
  },
};
