import { http } from '@/shared/api/http';
import type { PagedResponse } from '@/shared/types/api';
import type { Hotel } from '@/entities/hotel/model/types';

interface HotelListParams {
  page: number;
  size: number;
}

interface HotelSearchParams extends HotelListParams {
  country: string;
  minRating: number;
}

export const hotelsApi = {
  list: async (params: HotelListParams) => {
    const response = await http.get<PagedResponse<Hotel>>('/hotels', { params });
    return response.data;
  },

  search: async (params: HotelSearchParams) => {
    const response = await http.get<PagedResponse<Hotel>>('/hotels/search', { params });
    return response.data;
  },

  getById: async (hotelId: number) => {
    const response = await http.get<Hotel>(`/hotels/${hotelId}`);
    return response.data;
  },

  create: async (payload: Hotel) => {
    const response = await http.post<Hotel>('/hotels', payload);
    return response.data;
  },

  update: async (hotelId: number, payload: Hotel) => {
    const response = await http.put<Hotel>(`/hotels/${hotelId}`, payload);
    return response.data;
  },

  remove: async (hotelId: number) => {
    await http.delete(`/hotels/${hotelId}`);
  },
};
