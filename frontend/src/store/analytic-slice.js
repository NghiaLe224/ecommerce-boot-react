import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getAnalyticApi } from "../api/analytic.api";


export const fetchAnalytics = createAsyncThunk(
    'analytics/getAnalytics',
    async (_, thunkAPI) => {
        try {
            const res = await getAnalyticApi();
            return res.data;
        } catch (error) {
            const msg = error?.response?.data?.message || 'Fetch analytics failed';
            return thunkAPI.rejectWithValue(msg);
        }
    }
)

const initialState = {
    analytics: null,
    loading: false,
    error: null,
}

const analyticsSlice = createSlice({
    name: 'analytics',
    initialState,
    reducers:{},
    extraReducers: (builder) => {
        builder
        .addCase(fetchAnalytics.pending, (state) => {
            state.loading = true;
            state.error = null;
        })
        .addCase(fetchAnalytics.fulfilled, (state, action) => {
            state.loading = false;
            state.analytics = action.payload;
        })
        .addCase(fetchAnalytics.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload || "fetch analytics failed";
        })
    }
})

export default analyticsSlice.reducer;