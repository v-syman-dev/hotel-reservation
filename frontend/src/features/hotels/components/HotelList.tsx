import { useEffect, useState } from 'react';
import { HotelService } from '../api';
import type { HotelDto } from '../../../entities/types';

export const HotelList = () => {
  const [hotels, setHotels] = useState<HotelDto[]>([]);

  useEffect(() => {
    HotelService.findAll().then(res => {
      setHotels(res.data.content);
    });
  }, []);

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Отели</h2>
      <div className="grid gap-4">
        {hotels.map(hotel => (
          <div key={hotel.id} className="border p-4 rounded shadow">
            <h3>{hotel.name}</h3>
            <p>Рейтинг: {hotel.rating} ⭐</p>
          </div>
        ))}
      </div>
    </div>
  );
};
