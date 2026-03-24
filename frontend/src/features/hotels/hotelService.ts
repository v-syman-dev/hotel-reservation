import { api } from '../../common/api/base';
import type { HotelDto } from '../../entities/types';

export const hotelService = {
  getAll: (page = 0, size = 100) => 
    api.get(`/hotels?page=${page}&size=${size}`).then(res => res.data),
    
  getById: (id: number) => 
    api.get<HotelDto>(`/hotels/${id}`).then(res => res.data),
    
  create: (hotel: HotelDto) => 
    api.post<HotelDto>('/hotels', hotel).then(res => res.data),
    
  search: (country: string, minRating: number) =>
    api.get('/hotels/search', { params: { country, minRating } }).then(res => res.data),
};
