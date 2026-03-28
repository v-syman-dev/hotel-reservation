import type { Address } from '@/entities/address/model/types';
import type { Convenience } from '@/entities/convenience/model/types';
import type { Room } from '@/entities/room/model/types';

export interface Hotel {
  id?: number;
  name: string;
  address: Address;
  rating: number;
  rooms: Room[];
  conveniences: Convenience[];
}
