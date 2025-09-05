import api from "./api";

export const saveAddress = (data) => {
  return api.post("/addresses", data);
};

export const getAddresses = () => {
  return api.get("/users/addresses");
};

export const updateAddressApi = (data) => {
  return api.put(`addresses/${data.id}`, data);
};

export const removeAddress = (id) => {
  return api.delete(`addresses/${id}`);
};
