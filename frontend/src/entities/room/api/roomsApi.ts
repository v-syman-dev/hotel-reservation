import { http } from '@/shared/api/http';
import type { Room } from '@/entities/room/model/types';
import type { PagedResponse } from '@/shared/types/api';

export const roomsApi = {
  listByHotel: async (hotelId: number) => {
    const response = await http.get<Room[]>(`/hotels/${hotelId}/rooms`);
    return response.data;
  },

  listByHotelPaged: async (hotelId: number, page: number, size: number) => {
    const response = await http.get<PagedResponse<Room>>(`/hotels/${hotelId}/rooms/paged`, {
      params: { page, size },
    });
    return response.data;
  },

  create: async (hotelId: number, payload: Room) => {
    const response = await http.post<Room>(`/hotels/${hotelId}/rooms`, payload);
    return response.data;
  },

  update: async (payload: Room) => {
    const response = await http.put<Room>('/rooms', payload);
    return response.data;
  },

  remove: async (roomId: number) => {
    await http.delete(`/rooms/${roomId}`);
  },
};
