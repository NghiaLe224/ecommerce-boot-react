import api from "./api";

export const fetchCategories = (params = {}) => {
    return api.get(`/public/categories`, {params});
}

export const updateCategoryApi = ({categoryId, data}) => {
    return api.put(`/admin/categories/${categoryId}`, data);
}

export const createCategoryApi = (data) => {
    return api.post('/admin/categories', data);
}

export const deleteCategoryApi = (categoryId) => {
    return api.delete(`/admin/categories/${categoryId}`);
}
