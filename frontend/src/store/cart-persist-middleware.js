const STORAGE_KEY = 'cart';

const saveCartToLocalStorage = (cartState) => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(cartState));
  } catch (e) {
    console.error('Cart persist error', e);
  }
};

export const cartPersistMiddleware = (storeAPI) => (next) => (action) => {
  const result = next(action);

  if (action.type.startsWith('cart/')) {
    const { cart } = storeAPI.getState();
    saveCartToLocalStorage(cart);
  }

  return result;
};
