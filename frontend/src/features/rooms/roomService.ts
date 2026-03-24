import { api } from "../../common/api/base";
import type { RoomDto } from "../../entities/types";

export const roomService = {
  getByHotelId: (hotelId: number) => 
    api.get<RoomDto[]>(`/hotels/${hotelId}/rooms`).then(res => res.data),
    
  create: (hotelId: number, room: RoomDto) =>
    api.post<RoomDto>(`/hotels/${hotelId}/rooms`, room).then(res => res.data),
};
