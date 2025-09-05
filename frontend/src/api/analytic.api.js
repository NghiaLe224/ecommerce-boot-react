import api from "./api"

export const getAnalyticApi = () => {
    return api.get('/admin/analytics');
}