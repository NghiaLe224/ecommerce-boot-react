import { configureStore } from "@reduxjs/toolkit";
import  productReducer  from './product-slice'
import cartReducer from './cart-slice';
import authReducer from './auth-slice';
import addressReducer from './address-slice';
import paymentMethodReducer from './paymentMethod-slice'
import orderReducer from './order-slice';
import analyticReducer from './analytic-slice';
import categoryReducer from './category-slice';
import sellerReducer from './seller-slice';

const store = configureStore({
    reducer: {
        products: productReducer,
        cart: cartReducer,
        auth: authReducer,
        address: addressReducer,
        paymentMethod: paymentMethodReducer,
        analytic: analyticReducer,
        order: orderReducer,
        category: categoryReducer,
        seller: sellerReducer,
    },
});

export default store;