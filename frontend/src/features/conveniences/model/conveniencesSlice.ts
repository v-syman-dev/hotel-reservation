import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { conveniencesApi } from '@/entities/convenience/api/conveniencesApi';
import type { Convenience } from '@/entities/convenience/model/types';
import { getErrorMessage } from '@/shared/lib/errors';
import type { RequestStatus } from '@/shared/types/api';

interface ConveniencesState {
  items: Convenience[];
  status: RequestStatus;
  error: string | null;
  mutationStatus: RequestStatus;
  mutationError: string | null;
}

const initialState: ConveniencesState = {
  items: [],
  status: 'idle',
  error: null,
  mutationStatus: 'idle',
  mutationError: null,
};

export const fetchConveniences = createAsyncThunk(
  'conveniences/fetchConveniences',
  async (_: void, { rejectWithValue }) => {
    try {
      return await conveniencesApi.list();
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const createConvenience = createAsyncThunk(
  'conveniences/createConvenience',
  async (payload: Convenience, { rejectWithValue }) => {
    try {
      return await conveniencesApi.create(payload);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const updateConvenience = createAsyncThunk(
  'conveniences/updateConvenience',
  async (payload: Convenience, { rejectWithValue }) => {
    try {
      return await conveniencesApi.update(payload);
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

export const removeConvenience = createAsyncThunk(
  'conveniences/removeConvenience',
  async (convenienceId: number, { rejectWithValue }) => {
    try {
      await conveniencesApi.remove(convenienceId);
      return convenienceId;
    } catch (error) {
      return rejectWithValue(getErrorMessage(error));
    }
  },
);

const conveniencesSlice = createSlice({
  name: 'conveniences',
  initialState,
  reducers: {
    clearConvenienceMutationState(state) {
      state.mutationStatus = 'idle';
      state.mutationError = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchConveniences.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchConveniences.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.items = action.payload;
      })
      .addCase(fetchConveniences.rejected, (state, action) => {
        state.status = 'failed';
        state.error = (action.payload as string) ?? 'Failed to load conveniences.';
      })
      .addCase(createConvenience.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(createConvenience.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.items = [...state.items, action.payload];
      })
      .addCase(createConvenience.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to create convenience.';
      })
      .addCase(updateConvenience.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(updateConvenience.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.items = state.items.map((item) =>
          item.id === action.payload.id ? action.payload : item,
        );
      })
      .addCase(updateConvenience.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to update convenience.';
      })
      .addCase(removeConvenience.pending, (state) => {
        state.mutationStatus = 'loading';
        state.mutationError = null;
      })
      .addCase(removeConvenience.fulfilled, (state, action) => {
        state.mutationStatus = 'succeeded';
        state.items = state.items.filter((item) => item.id !== action.payload);
      })
      .addCase(removeConvenience.rejected, (state, action) => {
        state.mutationStatus = 'failed';
        state.mutationError = (action.payload as string) ?? 'Failed to delete convenience.';
      });
  },
});

export const { clearConvenienceMutationState } = conveniencesSlice.actions;
export const conveniencesReducer = conveniencesSlice.reducer;
