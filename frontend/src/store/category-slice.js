import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  createCategoryApi,
  deleteCategoryApi,
  fetchCategories,
  updateCategoryApi,
} from "../api/category.api";

export const fetchCategoriesPaging = createAsyncThunk(
  "categories/fetchAll",
  async ({ params }, thunkAPI) => {
    try {
      const res = await fetchCategories(params);
      return res.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message ||
          error.message ||
          "fetch all category failed"
      );
    }
  }
);


export const createCategoryDashboard = createAsyncThunk(
  "categories/create",
  async (data, thunkAPI) => {
    try {
      const res = await createCategoryApi(data);
      return res.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message ||
          error.message ||
          "create category failed"
      );
    }
  }
);

export const updateCategoryDashboard = createAsyncThunk(
  "categories/update",
  async ({ categoryId, data }, thunkAPI) => {
    try {
      const res = await updateCategoryApi({ categoryId, data });
      return res.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message ||
          error.message ||
          "update category failed"
      );
    }
  }
);

export const deleteCategoryDashboard = createAsyncThunk(
  "categories/delete",
  async (categoryId, thunkAPI) => {
    try {
      const res = await deleteCategoryApi(categoryId);
      return { categoryId, message: res.data?.message };
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data?.message ||
          error.message ||
          "delete category failed"
      );
    }
  }
);

const initialState = {
  loading: false,
  error: null,
  categories: [],
  pagination: {},
};

const categorySlice = createSlice({
  name: "category",
  initialState,
  reducers: {},
  extraReducers: (build) =>
    build
      // --- FETCH ALL ---
      .addCase(fetchCategoriesPaging.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCategoriesPaging.fulfilled, (state, action) => {
        const pageData = action.payload.data;
        
        console.log("pageData: ", pageData);
        
        state.loading = false;
        state.categories = pageData.content;
        state.pagination = {
          pageNumber: pageData.pageNumber,
          pageSize: pageData.pageSize,
          totalElements: pageData.totalElements,
          totalPages: pageData.totalPages,
          last: pageData.last,
        };
      })
      .addCase(fetchCategoriesPaging.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch categories.";
      })

      // --- CREATE ---
      .addCase(createCategoryDashboard.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createCategoryDashboard.fulfilled, (state, action) => {
        state.loading = false;
        // prepend để hiển thị ngay category mới nhất
        state.categories = [action.payload.data, ...state.categories];
      })
      .addCase(createCategoryDashboard.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to create category.";
      })

      // --- UPDATE ---
      .addCase(updateCategoryDashboard.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateCategoryDashboard.fulfilled, (state, action) => {
        state.loading = false;
        const updated = action.payload.data;
        console.log('for update:', action.payload.data);
        
        state.categories = state.categories.map((cat) =>
          cat.categoryId === updated.categoryId ? updated : cat
        );
      })
      .addCase(updateCategoryDashboard.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to update category.";
      })

      // --- DELETE ---
      .addCase(deleteCategoryDashboard.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteCategoryDashboard.fulfilled, (state, action) => {
        state.loading = false;
        const { categoryId } = action.payload;
        state.categories = state.categories.filter(
          (cat) => cat.categoryId !== categoryId
        );
      })
      .addCase(deleteCategoryDashboard.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to delete category.";
      }),
});

export default categorySlice.reducer;
