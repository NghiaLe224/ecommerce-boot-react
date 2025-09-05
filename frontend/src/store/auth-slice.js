import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { login, register, logout as logoutBackend } from '../api/auth.api';

// LOGIN
export const loginUser = createAsyncThunk(
  'auth/login',
  async (credentials, thunkAPI) => {
    try {
      const res = await login(credentials);
      const { accessToken, userResponse } = res.data;
      localStorage.setItem('jwtToken', accessToken);
      return { accessToken, userResponse };
    } catch (err) {
      const msg = err?.response?.data?.message || 'Login failed';
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// REGISTER
export const registerUser = createAsyncThunk(
  'auth/register',
  async (credentials, thunkAPI) => {
    try {
      const res = await register(credentials);
      
      // Kiểm tra xem response có thành công không (status === 200)
      if (res.status === 200) {
        const { accessToken, userResponse } = res.data.data;
        console.log(res);
        
        // Lưu token vào localStorage
        localStorage.setItem('jwtToken', accessToken);
        
        // Trả về accessToken và userResponse
        return { accessToken, userResponse };
      } else {
        // Trường hợp response không thành công (mã lỗi khác 200)
        return thunkAPI.rejectWithValue('Request failed');
      }
      
    } catch (err) {
      const msg = err?.response?.data?.message || 'Register failed';
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// LOGOUT
export const logoutUser = createAsyncThunk(
  'auth/logout',
  async (_, thunkAPI) => {
    try {
      await logoutBackend(); // Gọi BE để xoá cookie refresh
      return true; // Không cần trả data
    } catch (err) {
      const msg = err?.response?.data?.message || 'Logout failed';
      return thunkAPI.rejectWithValue(msg);
    }
  }
);

// INITIAL STATE
const initialState = {
  token: typeof window !== 'undefined' ? localStorage.getItem('jwtToken') : null,
  userResponse:
    typeof window !== 'undefined' && localStorage.getItem('userResponse')
      ? JSON.parse(localStorage.getItem('userResponse'))
      : null,
  status: 'idle',
  error: null,
  selectedUserCheckoutAddress: null,
};

// SLICE
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // Fallback logout nếu muốn gọi thủ công
    logout(state) {
      localStorage.removeItem('jwtToken');
      state.token = null;
      state.userResponse = null;
      state.status = 'idle';
      state.error = null;
    },
    selectUserAddress(state, action){
      state.selectedUserCheckoutAddress = action.payload;
    },
    deleteUserAddress(state){
      state.selectedUserCheckoutAddress = null;
    }
  },
  extraReducers: (builder) => {
    builder
      // LOGIN
      .addCase(loginUser.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.token = action.payload.accessToken;
        state.userResponse = action.payload.userResponse;
        state.error = null;
        localStorage.setItem('userResponse', JSON.stringify(action.payload.userResponse));
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload || 'Login failed';
      })

      // REGISTER
      .addCase(registerUser.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.token = action.payload.accessToken;
        state.userResponse = action.payload.userResponse;
        state.error = null;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload || 'Register failed';
      })

      // LOGOUT
      .addCase(logoutUser.fulfilled, (state) => {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userResponse');
        state.token = null;
        state.userResponse = null;
        state.status = 'idle';
        state.error = null;
      })
      .addCase(logoutUser.rejected, (state, action) => {
        state.error = action.payload || 'Logout failed';
      });
  },
});

export const { logout, selectUserAddress, deleteUserAddress } = authSlice.actions;
export default authSlice.reducer;
