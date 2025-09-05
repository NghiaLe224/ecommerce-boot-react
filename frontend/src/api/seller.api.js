import api from "./api"

export const fetchSellersApi = (params = {}) => {
    return api.get(`/admin/seller`, {params})
}

export const createSellersApi = (data) => {
    return api.post('/admin/seller', data)
}