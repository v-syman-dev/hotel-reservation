export interface AddressDto {
  country: string;
  city: string;
  street: string;
}

export interface HotelDto {
  id?: number;
  name: string;
  description: string;
  rating: number;
  address: AddressDto;
}

export interface RoomDto {
  id?: number;
  number: string;
  type: string;
  price: number;
  hotelId?: number;
}

export interface BookingDto {
  id?: number;
  checkIn: string;
  checkOut: string;
  guestName: string;
}

export interface ConvenienceDto {
  id?: number;
  name: string;
}

export interface RoomDto {
  id?: number;
  number: string;
  type: string;
  pricePerNight: number;
}
