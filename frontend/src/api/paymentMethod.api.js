import api from "./api"

export const getPaymentMethods = () => {
    return api.get('/payment-methods');
}