package com.fortune.project.controller;

import com.fortune.project.constant.AppConstant;
import com.fortune.project.dto.request.address.AddressCreateRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.service.AddressService;
import com.fortune.project.util.AuthUtil;
import com.fortune.project.util.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<?> createNewAddress(
            @Valid @RequestBody AddressCreateRequest request
            ){
        ApiResponse<?> response = addressService.createAddress(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> viewAllAddresses(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE + "", required = false) int page,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SIZE + "", required = false) int size,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_BY_ID, required = false) String sortBy
    ){
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        ApiResponse<?> response = addressService.viewAllAddress(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<?> getAddress(
            @PathVariable Long addressId
    ){
        ApiResponse<?> response = addressService.getAddressById(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<?> getUserAddresses(){
        Long userId = authUtil.loggedInId();
        ApiResponse<?> response = addressService.getUserAddresses(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressCreateRequest addressCreateRequest
    ){
        ApiResponse<?> response = addressService.updateAddress(addressId, addressCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long addressId
    ){
        ApiResponse<?> response = addressService.deleteAddressById(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
