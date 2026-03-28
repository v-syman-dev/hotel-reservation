import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { bookingsApi } from '@/entities/booking/api/bookingsApi';
import type { Booking } from '@/entities/booking/model/types';
import { hotelsApi } from '@/entities/hotel/api/hotelsApi';
import type { Hotel } from '@/entities/hotel/model/types';
import { roomsApi } from '@/entities/room/api/roomsApi';
import type { Room } from '@/entities/room/model/types';
import { getErrorMessage } from '@/shared/lib/errors';
import type { RequestStatus } from '@/shared/types/api';

interface HotelDetailsState {
  hotel: Hotel | null;
  status: RequestStatus;
  error: string | null;
  mutationStatus: RequestStatus;
  mutationError: string | null;
  bookingsByRoom: Record<number, Booking[]>;
  bookingStatusByRoom: Record<number, RequestStatus>;
  bookingErrorByRoom: Record<number, string | null>;
}

const initialState: HotelDetailsState = {
  hotel: null,
  status: 'idle',
  error: null,
  mutationStatus: 'idle',
  mutationError: null,
  bookingsByRoom: {},
  bookingStatusByRoom: {},
  bookingErrorByRoom: {},
};

export const fetchHotelDetails = createAsyncThunk(
  'hotelDetails/fetchHotelDetails',
  async (hotelId: number, { rejectWithValue }) => {
    try {
      return await hotelsApi.getById(hotelId);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const updateHotel = createAsyncThunk(
  'hotelDetails/updateHotel',
  async ({ hotelId, payload }: { hotelId: number; payload: Hotel }, { rejectWithValue }) => {
    try {
      return await hotelsApi.update(hotelId, payload);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const deleteHotel = createAsyncThunk(
  'hotelDetails/deleteHotel',
  async (hotelId: number, { rejectWithValue }) => {
    try {
      await hotelsApi.remove(hotelId);
      return hotelId;
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const createRoom = createAsyncThunk(
  'hotelDetails/createRoom',
  async ({ hotelId, room }: { hotelId: number; room: Room }, { rejectWithValue }) => {
    try {
      await roomsApi.create(hotelId, room);
      return await hotelsApi.getById(hotelId);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const updateRoom = createAsyncThunk(
  'hotelDetails/updateRoom',
  async ({ hotelId, room }: { hotelId: number; room: Room }, { rejectWithValue }) => {
    try {
      await roomsApi.update(room);
      return await hotelsApi.getById(hotelId);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const deleteRoom = createAsyncThunk(
  'hotelDetails/deleteRoom',
  async ({ hotelId, roomId }: { hotelId: number; roomId: number }, { rejectWithValue }) => {
    try {
      await roomsApi.remove(roomId);
      return await hotelsApi.getById(hotelId);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const fetchRoomBookings = createAsyncThunk(
  'hotelDetails/fetchRoomBookings',
  async (roomId: number, { rejectWithValue }) => {
    try {
      const bookings = await bookingsApi.listByRoom(roomId);
      return { roomId, bookings };
    } catch (error) {
      return rejectWithValue({ roomId, message: getErrorMessage(error) });
    }
  },
);

export const createBooking = createAsyncThunk(
  'hotelDetails/createBooking',
  async ({ roomId, booking }: { roomId: number; booking: Booking }, { rejectWithValue }) => {
    try {
      await bookingsApi.create(roomId, booking);
      const bookings = await bookingsApi.listByRoom(roomId);
      return { roomId, bookings };
    } catch (error) {
      return rejectWithValue({ roomId, message: getErrorMessage(error) });
    }
  },
);

export const updateBooking = createAsyncThunk(
  'hotelDetails/updateBooking',
  async (
    { roomId, bookingId, booking }: { roomId: number; bookingId: number; booking: Booking },
    { rejectWithValue },
  ) => {
    try {
      await bookingsApi.update(roomId, bookingId, booking);
      const bookings = await bookingsApi.listByRoom(roomId);
      return { roomId, bookings };
    } catch (error) {
      return rejectWithValue({ roomId, message: getErrorMessage(error) });
    }
  },
);

export const deleteBooking = createAsyncThunk(
  'hotelDetails/deleteBooking',
  async ({ roomId, bookingId }: { roomId: number; bookingId: number }, { rejectWithValue }) => {
    try {
      await bookingsApi.remove(roomId, bookingId);
      const bookings = await bookingsApi.listByRoom(roomId);
      return { roomId, bookings };
    } catch (error) {
      return rejectWithValue({ roomId, message: getErrorMessage(error) });
    }
  },
);

const hotelDetailsSlice = createSlice({
  name: 'hotelDetails',
  initialState,
  reducers: {
    clearHotelDetails(state) {
      state.hotel = null;
      state.status = 'idle';
      state.error = null;
      state.bookingsByRoom = {};
      state.bookingStatusByRoom = {};
      state.bookingErrorByRoom = {};
    },
    clearHotelDetailsMutationState(state) {
      state.mutationStatus = 'idle';
      state.mutationError = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchHotelDetails.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchHotelDetails.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.hotel = action.payload;
      })
      .addCase(fetchHotelDetails.rejected, (state, action) => {
        state.status = 'failed';
        state.error = (action.payload as string) ?? 'Failed to load hotel details.';
      })
      .addCase(updateHotel.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(updateHotel.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.hotel = action.payload;
      })
      .addCase(updateHotel.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to update hotel.';
      })
      .addCase(deleteHotel.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(deleteHotel.fulfilled, (state) => {
        state.mutationStatus = 'succeeded';
        state.hotel = null;
      })
      .addCase(deleteHotel.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to delete hotel.';
      })
      .addCase(createRoom.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(createRoom.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.hotel = action.payload;
      })
      .addCase(createRoom.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to create room.';
      })
      .addCase(updateRoom.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(updateRoom.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.hotel = action.payload;
      })
      .addCase(updateRoom.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to update room.';
      })
      .addCase(deleteRoom.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(deleteRoom.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.hotel = action.payload;
      })
      .addCase(deleteRoom.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to delete room.';
      })
      .addCase(fetchRoomBookings.pending, (state, action) => {
        state.bookingStatusByRoom[action.meta.arg] = 'loading';
        state.bookingErrorByRoom[action.meta.arg] = null;
      })
      .addCase(fetchRoomBookings.fulfilled, (state, action) => {
        state.bookingStatusByRoom[action.payload.roomId] = 'succeeded';
        state.bookingsByRoom[action.payload.roomId] = action.payload.bookings;
      })
      .addCase(fetchRoomBookings.rejected, (state, action) => {
        const payload = action.payload as { roomId: number; message: string } | undefined;

        if (!payload) {
          return;
        }

        state.bookingStatusByRoom[payload.roomId] = 'failed';
        state.bookingErrorByRoom[payload.roomId] = payload.message;
      })
      .addCase(createBooking.pending, (state, action) => {
        state.bookingStatusByRoom[action.meta.arg.roomId] = 'loading';
        state.bookingErrorByRoom[action.meta.arg.roomId] = null;
      })
      .addCase(createBooking.fulfilled, (state, action) => {
        state.bookingStatusByRoom[action.payload.roomId] = 'succeeded';
        state.bookingsByRoom[action.payload.roomId] = action.payload.bookings;
      })
      .addCase(createBooking.rejected, (state, action) => {
        const payload = action.payload as { roomId: number; message: string } | undefined;

        if (!payload) {
          return;
        }

        state.bookingStatusByRoom[payload.roomId] = 'failed';
        state.bookingErrorByRoom[payload.roomId] = payload.message;
      })
      .addCase(updateBooking.pending, (state, action) => {
        state.bookingStatusByRoom[action.meta.arg.roomId] = 'loading';
        state.bookingErrorByRoom[action.meta.arg.roomId] = null;
      })
      .addCase(updateBooking.fulfilled, (state, action) => {
        state.bookingStatusByRoom[action.payload.roomId] = 'succeeded';
        state.bookingsByRoom[action.payload.roomId] = action.payload.bookings;
      })
      .addCase(updateBooking.rejected, (state, action) => {
        const payload = action.payload as { roomId: number; message: string } | undefined;

        if (!payload) {
          return;
        }

        state.bookingStatusByRoom[payload.roomId] = 'failed';
        state.bookingErrorByRoom[payload.roomId] = payload.message;
      })
      .addCase(deleteBooking.pending, (state, action) => {
        state.bookingStatusByRoom[action.meta.arg.roomId] = 'loading';
        state.bookingErrorByRoom[action.meta.arg.roomId] = null;
      })
      .addCase(deleteBooking.fulfilled, (state, action) => {
        state.bookingStatusByRoom[action.payload.roomId] = 'succeeded';
        state.bookingsByRoom[action.payload.roomId] = action.payload.bookings;
      })
      .addCase(deleteBooking.rejected, (state, action) => {
        const payload = action.payload as { roomId: number; message: string } | undefined;

        if (!payload) {
          return;
        }

        state.bookingStatusByRoom[payload.roomId] = 'failed';
        state.bookingErrorByRoom[payload.roomId] = payload.message;
      });
  },
});

export const { clearHotelDetails, clearHotelDetailsMutationState } = hotelDetailsSlice.actions;
export const hotelDetailsReducer = hotelDetailsSlice.reducer;
