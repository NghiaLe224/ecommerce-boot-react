import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getPaymentMethods } from "../api/paymentMethod.api";


export const fetchPaymentMethods = createAsyncThunk(
    'paymentMethod/fetch',
    async (_, thunkAPI) => {
        try {
            const res = await getPaymentMethods();
            console.log("res:", res);
            const paymentMethods = res.data.data;
            console.log("paymentMethods:", paymentMethods);
            return paymentMethods;
        } catch (error) {
            const msg = err?.response?.data?.message || 'Fetch payment methods failed';
            return thunkAPI.rejectWithValue(msg);
        }
    }
)

const initialState = {
    paymentMethods: "VNPay",
    loading: false,
    error: null
}

const paymentMethodSlice = createSlice({
    name: 'paymentMethod',
    initialState,
    reducers: {
        addPaymentMethod(state, action){
            state.paymentMethod = action.payload;
        }
    },
    extraReducers: builder => {
        builder
        .addCase(fetchPaymentMethods.pending, (state) => {
            state.loading = true;
            state.error = null;
        })
        .addCase(fetchPaymentMethods.fulfilled, (state, action) => {
            state.loading = false,
            state.err = null,
            state.paymentMethods = action.payload?.data,
            console.log("action.payload.data:", action.payload.data);
        })
        .addCase(fetchPaymentMethods.rejected, (state, action) => {
            state.error = action.payload;
            state.loading = false;
        })
    }
})

export const {addPaymentMethod} = paymentMethodSlice.actions;
export default paymentMethodSlice.reducer;