// cart-slice.js (đã sửa)
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  addToCart as apiAddToCart,
  getCart,
  updateItemQuantity,
  removeItem,
  syncCart,
  removeAllItemsApi,
} from "../api/cart.api";

const STORAGE_KEY = "cart";

const saveToStorage = (state) => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
};

const loadFromStorage = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

const initialState = loadFromStorage() || {
  cartId: null,
  items: [],
  loading: false,
  error: null,
  totalPrice: 0,
};

// Helper tính tổng tiền
const recalcTotals = (state) => {
  state.totalPrice = state.items.reduce(
    (sum, item) => sum + item.quantity * Number(item.snapshotPrice || 0),
    0
  );
};

export const loadCartFromServer = createAsyncThunk(
  "cart/loadFromServer",
  async () => {
    const res = await getCart();
    return res.data.data;
  }
);

export const addToCartServer = createAsyncThunk(
  "cart/addToCartServer",
  async ({ id, quantity }) => {
    await apiAddToCart(id, quantity);
    return { id, quantity };
  }
);

export const updateQuantityServer = createAsyncThunk(
  "cart/updateQuantityServer",
  async ({ productId, quantity }) => {
    await updateItemQuantity(productId, quantity);
    return { productId, quantity };
  }
);

export const removeFromCartServer = createAsyncThunk(
  "cart/removeFromCartServer",
  async (id) => {
    await removeItem(id);
    return id;
  }
);

export const removeAllItemsFromCartServer = createAsyncThunk(
  "cart/removeAllItemsServer",
  async (_, thunkAPI) => {
    try {
      await removeAllItemsApi();
      return true;
    } catch (err) {
      const msg = err?.response?.data?.message || "Remove all items failed";
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

export const syncCartToServer = createAsyncThunk(
  "cart/syncCartToServer",
  async (_, thunkAPI) => {
    const cart = thunkAPI.getState().cart;
    const items = cart.items.map(({ productId, quantity }) => ({
      id: productId,
      quantity,
    }));
    await syncCart(items);
  }
);

const cartSlice = createSlice({
  name: "cart",
  initialState,
  reducers: {
    addToCartLocal(state, action) {
      const {
        id,
        name,
        imageUrl,
        quantity = 1,
        specialPrice,
        price,
      } = action.payload;
      const snapshotPrice = specialPrice || price; // GHI CHÚ: sửa để lưu snapshotPrice chính xác
      const existing = state.items.find((item) => item.id === id);
      if (existing) {
        existing.quantity += quantity;
      } else {
        state.items.push({
          id,
          name,
          imageUrl,
          quantity,
          snapshotPrice,
        });
      }
      recalcTotals(state);
      saveToStorage(state);
    },

    setQuantityLocal(state, action) {
      const { id, quantity } = action.payload;
      const item = state.items.find((i) => i.id === id);
      if (item) {
        item.quantity = Math.max(1, quantity);
      }
      recalcTotals(state);
      saveToStorage(state);
    },

    removeFromCartLocal(state, action) {
      state.items = state.items.filter((item) => item.id !== action.payload);
      recalcTotals(state);
      saveToStorage(state);
    },

    clearCart(state) {
      state.items = [];
      state.totalPrice = 0;
      localStorage.removeItem(STORAGE_KEY);
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(loadCartFromServer.fulfilled, (state, action) => {
        const data = action.payload;
        //chuyển đổi dữ liệu từ server (CartItemResponse[]) sang Redux
        state.items = data.items.map((item) => ({
          id: item.productId ?? `deleted-${item.name}`,
          productId: item.productId,
          name: item.name,
          imageUrl: `http://localhost:8080${item.imageUrl}`,
          snapshotPrice: item.snapshotPrice,
          quantity: item.quantity,
          deleted: item.deleted,
          priceChanged: item.priceChanged,
          subTotal: item.subTotal,
        }));
        state.totalPrice = data.totalPrice; // đồng bộ totalPrice từ server luôn
        state.cartId = data.cartId;
        saveToStorage(state);
      })
      .addCase(removeFromCartServer.fulfilled, (state, action) => {
        state.items = state.items.filter((i) => i.id !== action.payload);
        recalcTotals(state);
        saveToStorage(state);
      })
      .addCase(updateQuantityServer.fulfilled, (state, action) => {
        const { productId, quantity } = action.payload;
        const item = state.items.find((i) => i.productId === productId);
        if (item) item.quantity = quantity;
        recalcTotals(state);
        saveToStorage(state);
      })
      .addCase(removeAllItemsFromCartServer.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(removeAllItemsFromCartServer.fulfilled, (state) => {
        state.loading = false;
        state.items = [];
        state.totalPrice = 0;
        saveToStorage(state);
      })
      .addCase(removeAllItemsFromCartServer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Remove all items failed";
      });
  },
});

export const {
  addToCartLocal,
  setQuantityLocal,
  removeFromCartLocal,
  clearCart,
} = cartSlice.actions;

export default cartSlice.reducer;
