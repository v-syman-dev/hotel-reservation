import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from '@/app/layout/AppLayout';
import { ConveniencesPage } from '@/pages/ConveniencesPage';
import { DashboardPage } from '@/pages/DashboardPage';
import { HotelDetailsPage } from '@/pages/HotelDetailsPage';

export function AppRouter() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<DashboardPage />} />
        <Route path="/hotels/:hotelId" element={<HotelDetailsPage />} />
        <Route path="/conveniences" element={<ConveniencesPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
