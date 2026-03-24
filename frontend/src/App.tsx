import { Routes, Route } from 'react-router-dom';
import { HotelListPage } from './pages/HotelListPage';
import { HotelRoomsPage } from './pages/HotelRoomsPage'; // Импортируй страницу комнат

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<HotelListPage />} />

        <Route path="/hotels/:hotelId" element={<HotelRoomsPage />} />

        <Route path="/hotels/:hotelId/rooms" element={<HotelRoomsPage />} />

        <Route path="*" element={<div>404: Страница не найдена, бро</div>} />
      </Routes>
    </>
  );
}

export default App;
