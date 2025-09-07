import axios from "axios";

// Xác định baseURL phù hợp tùy môi trường
const baseURL = import.meta.env.MODE === "development"
  ? import.meta.env.VITE_BACK_END_URL || "http://localhost:8080/api"
  : "/api";

const api = axios.create({
  baseURL,
  withCredentials: true,
});

let isRefreshing = false;
let refreshSubscribers = [];

const onRefreshed = (newToken) => {
  refreshSubscribers.forEach((callback) => callback(newToken));
  refreshSubscribers = [];
};

const addRefreshSubscriber = (callback) => {
  refreshSubscribers.push(callback);
};

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("jwtToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          addRefreshSubscriber((newToken) => {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            resolve(api(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const res = await api.post("/auth/refresh");
        const newAccessToken = res.data.accessToken;
        localStorage.setItem("jwtToken", newAccessToken);
        api.defaults.headers.Authorization = `Bearer ${newAccessToken}`;
        onRefreshed(newAccessToken);
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem("jwtToken");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
