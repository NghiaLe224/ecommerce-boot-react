import api from "./api";

export const addToCart = (productId, quantity) => {
  return api.post(`/carts/products/${productId}/quantity/${quantity}`);
};

export const getCart = () => {
  return api.get('/carts/users/cart');
};

export const updateItemQuantity = (productId, quantity) => {
  return api.put('/carts/items', { productId, quantity });
};

export const removeItem = (productId) => {
  return api.delete(`/carts/items/${productId}`);
};

export const syncCart = (items) => {
  return api.put('/carts/sync', { items }); // items: [{ id, quantity }]
};

export const removeAllItemsApi = () => {
  return api.delete('/carts/items')
}
