import {api} from '../../common/api/base';
import type { HotelDto } from '../../entities/types';

export const HotelService = {
  findAll: (page = 0, size = 10) => 
    api.get(`/hotels?page=${page}&size=${size}`),

  findById: (id: number) => 
    api.get<HotelDto>(`/hotels/${id}`),

  create: (data: HotelDto) => 
    api.post<HotelDto>('/hotels', data),

  search: (country: string, minRating: number) =>
    api.get(`/hotels/search`, { params: { country, minRating } })
};
