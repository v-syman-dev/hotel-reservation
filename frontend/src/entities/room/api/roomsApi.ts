import { http } from '@/shared/api/http';
import type { Room } from '@/entities/room/model/types';

export const roomsApi = {
  listByHotel: async (hotelId: number) => {
    const response = await http.get<Room[]>(`/hotels/${hotelId}/rooms`);
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
