import { useEffect, useState } from 'react';

import { Link } from 'react-router-dom';
import type { HotelDto } from '../entities/types';
import { hotelService } from '../features/hotels/hotelService';

export const HotelListPage = () => {
  const [hotels, setHotels] = useState<HotelDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    hotelService.getAll()
      .then(data => setHotels(data.content))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div>Загрузка отелей...</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h1>Список отелей</h1>
      <div style={{ display: 'grid', gap: '15px' }}>
        {hotels.map(hotel => (
          <div key={hotel.id} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px' }}>
            <h2>{hotel.name}</h2>
            <div>
              <div>{hotel?.address.country}</div>
              <div>{hotel?.address.city}</div>
              <div>{hotel?.address.street}</div>
            </div>
            <p>{hotel.description}</p>
            <p>Рейтинг: {hotel.rating} / 5</p>
            <Link to={`/hotels/${hotel.id}`}>Посмотреть номера</Link>
          </div>
        ))}
      </div>
    </div>
  );
};
