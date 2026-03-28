import type { Room } from '@/entities/room/model/types';

export interface Booking {
  id?: number;
  guestName: string;
  checkInDate: string;
  checkOutDate: string;
  room?: Room;
}
