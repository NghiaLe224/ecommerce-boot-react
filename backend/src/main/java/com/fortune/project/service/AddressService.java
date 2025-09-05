package com.fortune.project.service;

import com.fortune.project.dto.request.address.AddressCreateRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.entity.AddressEntity;
import org.springframework.data.domain.Pageable;

public interface AddressService {
    ApiResponse<?> createAddress(AddressCreateRequest request);

    ApiResponse<?> viewAllAddress(Pageable pageable);

    ApiResponse<?> getAddressById(Long addressId);

    ApiResponse<?> getUserAddresses(Long userId);

    ApiResponse<?> updateAddress(Long addressId, AddressCreateRequest addressCreateRequest);

    ApiResponse<?> deleteAddressById(Long addressId);

    AddressEntity findById(Long shippingAddressId);
}
