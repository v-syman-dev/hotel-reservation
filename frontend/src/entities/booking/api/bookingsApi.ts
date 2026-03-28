import { http } from '@/shared/api/http';
import type { Booking } from '@/entities/booking/model/types';

export const bookingsApi = {
  listByRoom: async (roomId: number) => {
    const response = await http.get<Booking[]>(`/rooms/${roomId}/bookings`);
    return response.data;
  },

  create: async (roomId: number, payload: Booking) => {
    const response = await http.post<Booking>(`/rooms/${roomId}/bookings`, payload);
    return response.data;
  },

  update: async (roomId: number, bookingId: number, payload: Booking) => {
    const response = await http.put<Booking>(`/rooms/${roomId}/bookings/${bookingId}`, payload);
    return response.data;
  },

  remove: async (roomId: number, bookingId: number) => {
    await http.delete(`/rooms/${roomId}/bookings/${bookingId}`);
  },
};
