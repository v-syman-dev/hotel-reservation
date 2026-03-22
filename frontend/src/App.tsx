import { useEffect, useState } from 'react';
import api from './api';
import type { Hotel, Convenience } from './types';

function App() {
  // 1. Хранилище данных (State)
  const [hotels, setHotels] = useState<Hotel[]>([]); // Все отели
  const [search, setSearch] = useState('');          // Для фильтрации
  const [loading, setLoading] = useState(true);

  // 2. Загрузка данных (аналог @EventListener(ApplicationReadyEvent.class))
  const fetchHotels = () => {
    setLoading(true);
    // Делаем GET запрос к твоему контроллеру
    api.get<Hotel[]>('/hotels')
      .then(res => {
        setHotels(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  };

  useEffect(() => { fetchHotels(); }, []);

  // 3. Операция DELETE (Часть CRUD)
  const deleteHotel = (id: number) => {
    if (window.confirm('Удалить этот отель?')) {
      api.delete(`/hotels/${id}`).then(() => fetchHotels());
    }
  };

  // 4. Логика фильтрации (Требование лабы)
  const filteredHotels = hotels.filter(h => 
    h.name.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <h1>Загрузка...</h1>;

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>Управление Отелями</h1>

      {/* ФИЛЬТРАЦИЯ */}
      <input 
        type="text" 
        placeholder="Поиск по названию..." 
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ marginBottom: '20px', padding: '8px', width: '300px' }}
      />

      <div style={{ display: 'grid', gap: '20px' }}>
        {filteredHotels.map(hotel => (
          <div key={hotel.id} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px' }}>
            <h2>{hotel.name} (Рейтинг: {hotel.rating})</h2>
            <p>Адрес: {hotel.address.city}, {hotel.address.street}</p>

            {/* ОТОБРАЖЕНИЕ ManyToMany (Conveniences) */}
            <div style={{ marginBottom: '10px' }}>
              <strong>Удобства: </strong>
              {hotel.conveniences.map(c => (
                <span key={c.id} style={{ background: '#e0e0e0', padding: '2px 8px', marginRight: '5px', borderRadius: '4px' }}>
                  {c.name}
                </span>
              ))}
            </div>

            {/* ОТОБРАЖЕНИЕ OneToMany (Rooms) */}
            <details>
              <summary>Показать номера ({hotel.rooms.length})</summary>
              <table border={1} style={{ width: '100%', marginTop: '10px', borderCollapse: 'collapse' }}>
                <thead>
                  <tr>
                    <th>№</th>
                    <th>Тип</th>
                    <th>Цена</th>
                  </tr>
                </thead>
                <tbody>
                  {hotel.rooms.map(room => (
                    <tr key={room.id}>
                      <td>{room.number}</td>
                      <td>{room.type}</td>
                      <td>{room.pricePerNight} $</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </details>

            <button 
              onClick={() => hotel.id && deleteHotel(hotel.id)}
              style={{ marginTop: '10px', color: 'red', cursor: 'pointer' }}
            >
              Удалить отель
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
