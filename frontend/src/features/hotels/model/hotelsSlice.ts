import { createAsyncThunk, createSlice, type PayloadAction } from '@reduxjs/toolkit';
import type { RootState } from '@/app/store';
import { hotelsApi } from '@/entities/hotel/api/hotelsApi';
import type { Hotel } from '@/entities/hotel/model/types';
import { getErrorMessage } from '@/shared/lib/errors';
import type { RequestStatus } from '@/shared/types/api';

interface HotelsState {
  items: Hotel[];
  status: RequestStatus;
  error: string | null;
  mutationStatus: RequestStatus;
  mutationError: string | null;
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  filters: {
    country: string;
    minRating: number;
  };
}

const initialState: HotelsState = {
  items: [],
  status: 'idle',
  error: null,
  mutationStatus: 'idle',
  mutationError: null,
  page: 0,
  size: 6,
  totalPages: 0,
  totalElements: 0,
  filters: {
    country: '',
    minRating: 0,
  },
};

export const fetchHotels = createAsyncThunk(
  'hotels/fetchHotels',
  async (_: void, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const { page, size, filters } = state.hotels;

      if (filters.country.trim()) {
        return await hotelsApi.search({
          page,
          size,
          country: filters.country.trim(),
          minRating: filters.minRating,
        });
      }

      return await hotelsApi.list({ page, size });
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const createHotel = createAsyncThunk(
  'hotels/createHotel',
  async (payload: Hotel, { rejectWithValue }) => {
    try {
      return await hotelsApi.create(payload);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const removeHotel = createAsyncThunk(
  'hotels/removeHotel',
  async (hotelId: number, { rejectWithValue }) => {
    try {
      await hotelsApi.remove(hotelId);
      return hotelId;
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

const hotelsSlice = createSlice({
  name: 'hotels',
  initialState,
  reducers: {
    setCountryFilter(state, action: PayloadAction<string>) {
      state.filters.country = action.payload;
      state.page = 0;
    },
    setMinRatingFilter(state, action: PayloadAction<number>) {
      state.filters.minRating = action.payload;
      state.page = 0;
    },
    setPage(state, action: PayloadAction<number>) {
      state.page = action.payload;
    },
    clearHotelMutationState(state) {
      state.mutationStatus = 'idle';
      state.mutationError = null;
    },
    resetServerFilters(state) {
      state.filters = {
        country: '',
        minRating: 0,
      };
      state.page = 0;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchHotels.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchHotels.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.items = action.payload.content;
        state.totalPages = action.payload.totalPages;
        state.totalElements = action.payload.totalElements;
      })
      .addCase(fetchHotels.rejected, (state, action) => {
        state.status = 'failed';
        state.error = (action.payload as string) ?? 'Failed to load hotels.';
      })
      .addCase(createHotel.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(createHotel.fulfilled, (state) => {
        state.mutationStatus = 'succeeded';
      })
      .addCase(createHotel.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to create hotel.';
      })
      .addCase(removeHotel.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(removeHotel.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.items = state.items.filter((hotel) => hotel.id !== action.payload);
        state.totalElements = Math.max(0, state.totalElements - 1);
      })
      .addCase(removeHotel.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to delete hotel.';
      });
  },
});

export const {
  clearHotelMutationState,
  resetServerFilters,
  setCountryFilter,
  setMinRatingFilter,
  setPage,
} = hotelsSlice.actions;

export const hotelsReducer = hotelsSlice.reducer;
