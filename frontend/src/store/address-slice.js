import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getAddresses,
  removeAddress,
  saveAddress,
  updateAddressApi,
} from "../api/address.api";

// --- Thunk actions ---

export const createAddress = createAsyncThunk(
  "address/createAddress",
  async (data, thunkAPI) => {
    try {
      const response = await saveAddress(data);
      return response.data?.data;
    } catch (error) {
      const msg = error?.response?.data?.message || "Tạo địa chỉ thất bại!";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const getAllAddresses = createAsyncThunk(
  "address/getAll",
  async (_, thunkAPI) => {
    try {
      const res = await getAddresses();
      return res?.data?.data || [];
    } catch (error) {
      const msg = error?.response?.data?.message || "Lấy danh sách địa chỉ thất bại!";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const updateAddress = createAsyncThunk(
  "address/update",
  async (data, thunkAPI) => {
    try {
      const response = await updateAddressApi(data);
      return response?.data?.data;
    } catch (error) {
      const msg = error?.response?.data?.message || "Cập nhật địa chỉ thất bại!";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const deleteAddress = createAsyncThunk(
  "address/delete",
  async (id, thunkAPI) => {
    try {
      const response = await removeAddress(id);
      return { id }; // Trả lại ID để xóa trong reducer
    } catch (error) {
      const msg = error?.response?.data?.message || "Xóa địa chỉ thất bại!";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// --- Initial state ---

const initialState = {
  addresses: [],
  loading: false,
  error: null,
};

// --- Slice ---

const addressSlice = createSlice({
  name: "address",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder

      // Create
      .addCase(createAddress.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createAddress.fulfilled, (state, action) => {
        state.loading = false;
        state.addresses.push(action.payload);
      })
      .addCase(createAddress.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Get All
      .addCase(getAllAddresses.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getAllAddresses.fulfilled, (state, action) => {
        state.loading = false;
        state.addresses = action.payload;
      })
      .addCase(getAllAddresses.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Update
      .addCase(updateAddress.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateAddress.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.addresses.findIndex(addr => addr.id === action.payload.id);
        if (index !== -1) {
          state.addresses[index] = action.payload;
        }
      })
      .addCase(updateAddress.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Delete
      .addCase(deleteAddress.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAddress.fulfilled, (state, action) => {
        state.loading = false;
        state.addresses = state.addresses.filter(addr => addr.id !== action.payload.id);
      })
      .addCase(deleteAddress.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default addressSlice.reducer;
