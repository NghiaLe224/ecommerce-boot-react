import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { createSellersApi, fetchSellersApi } from "../api/seller.api";

export const fetchSellers = createAsyncThunk(
  "sellers/fetchSellers",
  async (params, thunkAPI) => {
    try {
      const response = await fetchSellersApi(params);
      return response.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message || error.message || "Unknown error"
      );
    }
  }
);

export const createSeller = createAsyncThunk(
  "sellers/createSeller",
  async (data, thunkAPI) => {
    try {
      const res = await createSellersApi(data);
      return res.data;
    } catch (err) {
      return thunkAPI.rejectWithValue(
        err?.response?.data?.message || "Failed to create seller"
      );
    }
  }
);

const sellerSlice = createSlice({
  name: "sellers",
  initialState: {
    sellers: [],
    pagination: {},
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      // FETCH SELLERS
      .addCase(fetchSellers.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSellers.fulfilled, (state, action) => {
        const pageData = action.payload; 
        state.loading = false;
        state.sellers = pageData.content || [];
        console.log('pageData: ', pageData.content);
        
        state.pagination = {
          ...state.pagination,
          pageNumber: pageData.pageNumber,
          pageSize: pageData.pageSize,
          totalElements: pageData.totalElements,
          totalPages: pageData.totalPages,
          last: pageData.last,
        };
      })
      .addCase(fetchSellers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch sellers.";
      })

      // CREATE SELLER
      .addCase(createSeller.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createSeller.fulfilled, (state, action) => {
        state.loading = false;
        const newSeller = action.payload.data;
        // Nếu backend trả object seller -> push vào list
        if (newSeller) {
          state.sellers.unshift(newSeller);
        }
      })
      .addCase(createSeller.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to create seller.";
      });
  },
});

export default sellerSlice.reducer;
