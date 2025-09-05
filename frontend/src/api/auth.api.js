import api from './api';

export const login = (credentials) => {
  return api.post('/auth/login', credentials);
};

export const register = (credentials) => {
  return api.post('/auth/signup', credentials);
}

export const logout = () => {
  return api.post('/auth/logout');
}