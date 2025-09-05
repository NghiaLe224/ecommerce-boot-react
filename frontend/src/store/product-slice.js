import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  addNewProductApi,
  deleteProductApi,
  fetchProducts,
  getDashboardProductsApi,
  updateProductApi,
  updateProductImageApi,
} from "../api/product.api";
import { fetchCategories } from "../api/category.api";

// Thunk gọi API
export const getProducts = createAsyncThunk(
  "products/getProducts",
  async (params, thunkAPI) => {
    try {
      const response = await fetchProducts(params);
      return response.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message || error.message || "Unknown error"
      );
    }
  }
);

export const getCategories = createAsyncThunk(
  "products/getCategories",
  async (_, thunkAPI) => {
    try {
      const response = await fetchCategories();
      return response.data.data.content;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message || error.message || "Unknown error"
      );
    }
  }
);

export const getDashboardProducts = createAsyncThunk(
  "products/getDashboard",
  async ({ isAdmin, params }, thunkAPI) => {
    try {
      const res = await getDashboardProductsApi({ isAdmin, params });
      return res.data;
    } catch (err) {
      const msg =
        err?.response?.data?.message || "Failed to fetch dashboard products";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const updateProductFromDashboard = createAsyncThunk(
  "products/updateDashboardProduct",
  async ({ productId, data }, thunkAPI) => {
    try {
      const res = await updateProductApi(productId, data);
      return res.data;
    } catch (err) {
      return thunkAPI.rejectWithValue(
        err?.response?.data?.message || "Failed to update product"
      );
    }
  }
);

export const addNewProductFromDashboard = createAsyncThunk(
  "products/addDashboardProduct",
  async ({ data, categoryId, isAdmin }, thunkAPI) => {
    try {
      const res = await addNewProductApi(data, categoryId, isAdmin);
      return res.data;
    } catch (err) {
      return thunkAPI.rejectWithValue(
        err?.response?.data?.message || "Failed to create product"
      );
    }
  }
);

export const deleteProduct = createAsyncThunk(
  "products/deleteProduct",
  async ({ productId, isAdmin }, thunkAPI) => {
    try {
      const res = await deleteProductApi(productId, isAdmin);
      return { message: res.data.message, productId };
    } catch (error) {
      const msg = error?.response?.data?.message || "Failed to delete product";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const updateProductImageFromDashboard = createAsyncThunk(
  "products/updateProductImage",
  async ({ productId, data, isAdmin }, thunkAPI) => {
    try {
      const res = await updateProductImageApi(productId, data, isAdmin);
      return res.data;
    } catch (err) {
      return thunkAPI.rejectWithValue(
        err?.response?.data?.message || "Failed to update product image"
      );
    }
  }
);

const productSlice = createSlice({
  name: "products",
  initialState: {
    items: [],
    pagination: {},
    loading: false,
    categories: [],
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getProducts.fulfilled, (state, action) => {
        const pageData = action.payload.data;

        state.loading = false;
        state.items = pageData.content;
        state.pagination = {
          ...state.pagination,
          pageNumber: pageData.pageNumber,
          pageSize: pageData.pageSize,
          totalElements: pageData.totalElements,
          totalPages: pageData.totalPages,
          last: pageData.last,
        };
      })
      .addCase(getProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch products.";
      })
      .addCase(getCategories.fulfilled, (state, action) => {
        state.categories = action.payload;
      })
      .addCase(getCategories.rejected, (state, action) => {
        state.categories = []; // fallback rỗng nếu fail
      })
      .addCase(getDashboardProducts.fulfilled, (state, action) => {
        const pageData = action.payload.data;

        state.loading = false;
        state.items = pageData.content;
        state.pagination = {
          ...state.pagination,
          pageNumber: pageData.pageNumber,
          pageSize: pageData.pageSize,
          totalElements: pageData.totalElements,
          totalPages: pageData.totalPages,
          last: pageData.last,
        };
      })
      .addCase(getDashboardProducts.rejected, (state, action) => {
        state.loading = false;
        state.error =
          action.payload.data || "Failed to fetch dashboard products";
      })
      .addCase(updateProductFromDashboard.fulfilled, (state, action) => {
        const updated = action.payload.data;
        const index = state.items.findIndex((p) => p.id === updated.id);
        if (index !== -1) {
          state.items[index] = updated;
        }
      })
      .addCase(addNewProductFromDashboard.fulfilled, (state, action) => {
        const newProduct = action.payload.data;
        state.items.unshift(newProduct);
      })
      // DELETE PRODUCT
      .addCase(deleteProduct.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteProduct.fulfilled, (state, action) => {
        state.loading = false;
        state.items = state.items.filter(
          (item) => item.id !== action.payload.productId
        );
      })
      .addCase(deleteProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to delete product";
      })
      .addCase(updateProductImageFromDashboard.fulfilled, (state, action) => {
        const updated = action.payload.data;
        const index = state.items.findIndex((p) => p.id === updated.id);
        if (index !== -1) {
          state.items[index] = updated;
        }
      })
      .addCase(updateProductImageFromDashboard.rejected, (state, action) => {
        state.error = action.payload || "Failed to update product image";
      });
  },
});

export default productSlice.reducer;
