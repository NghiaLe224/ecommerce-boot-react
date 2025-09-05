import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  createOrderApi,
  getOrdersApi,
  getOrderDetailApi,
  cancelOrderApi,
  getAllUserOrdersApi,
  updateOrderStatusApi,
} from "../api/order.api";

// Create Order
export const createOrder = createAsyncThunk(
  "orders/create",
  async (data, thunkAPI) => {
    try {
      const res = await createOrderApi(data);
      return res.data;
    } catch (error) {
      const msg = error?.response?.data?.message || "Create order failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// Get Orders for current user
export const getOrders = createAsyncThunk(
  "orders/getAll",
  async (_, thunkAPI) => {
    try {
      const res = await getOrdersApi();
      return res.data; // List<OrderResponse>
    } catch (error) {
      const msg = error?.response?.data?.message || "Get orders failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const getAllUserOrders = createAsyncThunk(
  "/orders/allUserOrders",
  async ({ pageNumber, isAdmin }, thunkAPI) => {
    try {
      const res = await getAllUserOrdersApi({pageNumber, isAdmin});
      return res.data;
    } catch (error) {
      const msg = error?.response?.data?.message || "Get orders failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// Get Order Detail
export const getOrderDetail = createAsyncThunk(
  "orders/getDetail",
  async (orderId, thunkAPI) => {
    try {
      const res = await getOrderDetailApi(orderId);
      return res.data;
    } catch (error) {
      const msg = error?.response?.data?.message || "Get order detail failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// Cancel Order
export const cancelOrder = createAsyncThunk(
  "orders/cancel",
  async (orderId, thunkAPI) => {
    try {
      await cancelOrderApi(orderId);
      return orderId; // Trả lại ID đã hủy để xóa khỏi danh sách
    } catch (error) {
      const msg = error?.response?.data?.message || "Cancel order failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const updateOrderStatusFromDashboard = createAsyncThunk(
  "orders/updateStatus",
  async ({ orderId, orderStatus, isAdmin }, thunkAPI) => {
    try {
      const res = await updateOrderStatusApi({ orderId, orderStatus, isAdmin });
      return { orderId, orderStatus, message: res.data.message };
    } catch (error) {
      const msg = error?.response?.data?.message || "Update status failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

const initialState = {
  orders: [],
  currentOrder: null,
  pagination: {},
  loading: false,
  error: null,
};

const orderSlice = createSlice({
  name: "orders",
  initialState,
  reducers: {
    resetOrderState: (state) => {
      state.currentOrder = null;
      state.error = null;
      state.loading = false;
    },
  },
  extraReducers: (builder) => {
    builder
      // Create Order
      .addCase(createOrder.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createOrder.fulfilled, (state, action) => {
        state.loading = false;
        state.currentOrder = action.payload;
      })
      .addCase(createOrder.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Get Orders
      .addCase(getOrders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getOrders.fulfilled, (state, action) => {
        state.loading = false;
        state.orders = action.payload;
      })
      .addCase(getOrders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Get Order Detail
      .addCase(getOrderDetail.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getOrderDetail.fulfilled, (state, action) => {
        state.loading = false;
        state.currentOrder = action.payload;
      })
      .addCase(getOrderDetail.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Cancel Order
      .addCase(cancelOrder.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(cancelOrder.fulfilled, (state, action) => {
        state.loading = false;
        // loại bỏ đơn hàng bị hủy khỏi danh sách
        state.orders = state.orders.filter(
          (order) => order.id !== action.payload
        );
      })
      .addCase(cancelOrder.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      //get all user orders
      .addCase(getAllUserOrders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getAllUserOrders.fulfilled, (state, action) => {
        const pageData = action.payload;
        state.loading = false;
        state.orders = pageData.content;
        state.pagination = {
          ...state.pagination,
          pageNumber: pageData.pageNumber,
          pageSize: pageData.pageSize,
          totalElements: pageData.totalElements,
          totalPages: pageData.totalPages,
          last: pageData.last,
        };
      })
      .addCase(getAllUserOrders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(updateOrderStatusFromDashboard.fulfilled, (state, action) => {
        const { orderId, orderStatus } = action.payload;
        const index = state.orders.findIndex((o) => o.id === orderId);
        if (index !== -1) {
          state.orders[index].status = orderStatus;
        }
      });
  },
});

export const { resetOrderState } = orderSlice.actions;
export default orderSlice.reducer;
