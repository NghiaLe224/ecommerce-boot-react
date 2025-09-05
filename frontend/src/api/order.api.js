import api from './api'

export const createOrderApi = (data) => {
    return api.post('/orders', data);
}

export const getOrdersApi = () => {
    return api.get('/orders');
}

export const getOrderDetailApi = (id) => {
    return api.post(`/orders/${id}`);
}

export const cancelOrderApi = (id) => {
    return api.delete(`/orders/${id}`);
}

export const getAllUserOrdersApi = ({ pageNumber, isAdmin }) => {
  const endpoint = isAdmin ? `/admin/orders` : `/seller/orders`;
  return api.get(endpoint, { params: { pageNumber } });
};

export const updateOrderStatusApi = (data) => {
    const endpoint = data.isAdmin ? `/admin/orders/status` : `/seller/orders/status`;
  return api.put(endpoint, data);
};