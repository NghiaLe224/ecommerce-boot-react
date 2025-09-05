package com.fortune.project.service.impl;

import com.fortune.project.dto.request.address.AddressCreateRequest;
import com.fortune.project.dto.request.address.AddressResponse;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.entity.AddressEntity;
import com.fortune.project.entity.UserEntity;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.AddressRepository;
import com.fortune.project.service.AddressService;
import com.fortune.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    private final AddressRepository addressRepository;


    @Override
    public ApiResponse<?> createAddress(AddressCreateRequest request) {
        UserEntity user = authUtil.loggedInUser();

        AddressEntity address = modelMapper.map(request, AddressEntity.class);
        address.setId(null);
        address.setUser(user);
        AddressEntity saved = addressRepository.save(address);
        AddressResponse addressResponse = modelMapper.map(saved, AddressResponse.class);
        return new ApiResponse<>("Address saved", addressResponse, LocalDateTime.now());
    }

    @Override
    public ApiResponse<?> viewAllAddress(Pageable pageable) {
        Page<AddressEntity> addresses = addressRepository.findByIsDeletedFalse(pageable);
        Page<AddressResponse> addressResponses = addresses.map(address -> modelMapper.map(address, AddressResponse.class));
        var pagingResponse = new PagingResponse<>(addressResponses);
        return new ApiResponse<>("Fetched all address", pagingResponse, LocalDateTime.now());
    }

    @Override
    public ApiResponse<?> getAddressById(Long addressId) {
        AddressEntity address = addressRepository.findByIdAndIsDeletedFalse(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "address id", addressId));

        AddressResponse addressResponse = modelMapper.map(address, AddressResponse.class);
        return new ApiResponse<>("Fetched address", addressResponse, LocalDateTime.now());
    }

    @Override
    public ApiResponse<?> getUserAddresses(Long userId) {
        List<AddressEntity> addresses = addressRepository.findByUser_idAndIsDeletedFalse(userId);
        if (addresses.isEmpty()) {
            throw new ResourceNotFoundException("Address", "user id", userId);
        }
        List<AddressResponse> addressResponses = addresses.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class)).toList();
        return new ApiResponse<>("Fetched addresses", addressResponses, LocalDateTime.now());
    }

    @Override
    public ApiResponse<?> updateAddress(Long addressId, AddressCreateRequest request) {
        AddressEntity address = addressRepository.findByIdAndIsDeletedFalse(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "address id", addressId));

        modelMapper.map(request, address);
        AddressEntity saved = addressRepository.save(address);
        AddressResponse addressResponse = modelMapper.map(saved, AddressResponse.class);
        return new ApiResponse<>("Updated address", addressResponse, LocalDateTime.now());
    }

    @Override
    public ApiResponse<?> deleteAddressById(Long addressId) {
        AddressEntity address = addressRepository.findByIdAndIsDeletedFalse(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "address id", addressId));
        address.setIsDeleted(true);
        addressRepository.save(address);
        return new ApiResponse<>("Deleted address", null, LocalDateTime.now());
    }

    @Override
    public AddressEntity findById(Long shippingAddressId) {
        return addressRepository.findById(shippingAddressId).orElseThrow(() -> new ResourceNotFoundException("Address", "address id", shippingAddressId));
    }
}
