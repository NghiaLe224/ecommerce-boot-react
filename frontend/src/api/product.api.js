import api from './api'

export const fetchProducts = (params = {}) => {
    return api.get(`/public/products`, {params})
}

export const getDashboardProductsApi = ({ isAdmin, params = {} }) => {
  const endpoint = isAdmin ? "/admin/products" : "/seller/products";
  return api.get(endpoint, { params });
};

export const updateProductApi = (productId, data) => {
  return api.put(`/admin/products/${productId}`, data);
};

export const addNewProductApi = (data, categoryId, isAdmin) => {
  const formData = new FormData();
  const endpoint = isAdmin
    ? `/admin/categories/${categoryId}/product`
    : `/seller/categories/${categoryId}/product`;

  // Bọc phần product thành JSON gửi lên @RequestPart("product")
  formData.append(
    "product",
    new Blob([JSON.stringify(data)], { type: "application/json" })
  );

  return api.post(endpoint, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const deleteProductApi = (productId, isAdmin) => {
  const endpoint = isAdmin
    ? `/admin/products/${productId}`
    : `/seller/products/${productId}`;
  return api.delete(endpoint);
};

export const updateProductImageApi = (productId, data, isAdmin) => {
  const endpoint = isAdmin
    ? `/admin/products/${productId}/image`
    : `/seller/products/${productId}/image`;
  return api.put(endpoint, data, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};


