import { configureStore } from '@reduxjs/toolkit';
import { conveniencesReducer } from '@/features/conveniences/model/conveniencesSlice';
import { hotelDetailsReducer } from '@/features/hotel-details/model/hotelDetailsSlice';
import { hotelsReducer } from '@/features/hotels/model/hotelsSlice';

export const store = configureStore({
  reducer: {
    hotels: hotelsReducer,
    hotelDetails: hotelDetailsReducer,
    conveniences: conveniencesReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
