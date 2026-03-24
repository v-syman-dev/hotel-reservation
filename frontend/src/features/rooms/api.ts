import { api } from "../../common/api/base";
import type { RoomDto } from "../../entities/types";

export const roomApi = {
  getByHotel: (hotelId: number) => 
    api.get<RoomDto[]>(`/hotels/${hotelId}/rooms`).then(res => res.data),

  create: (hotelId: number, room: RoomDto) =>
    api.post<RoomDto>(`/hotels/${hotelId}/rooms`, room).then(res => res.data),

  update: (room: RoomDto) =>
    api.put<RoomDto>(`/rooms`, room).then(res => res.data),

  remove: (id: number) =>
    api.delete(`/rooms/${id}`).then(res => res.data),
};
