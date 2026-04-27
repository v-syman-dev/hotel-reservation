import { http } from '@/shared/api/http';
import type { Booking } from '@/entities/booking/model/types';
import type { PagedResponse } from '@/shared/types/api';

export const bookingsApi = {
  listByRoom: async (roomId: number) => {
    const response = await http.get<Booking[]>(`/rooms/${roomId}/bookings`);
    return response.data;
  },

  listByHotelPaged: async (hotelId: number, page: number, size: number) => {
    const response = await http.get<PagedResponse<Booking>>(`/hotels/${hotelId}/bookings`, {
      params: { page, size },
    });
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
