package com.fortune.project.service;

import com.fortune.project.entity.AddressEntity;

public interface ShipmentService {
    Double calculateShippingFee(Long shippingAddressId, Double subTotal);
}
