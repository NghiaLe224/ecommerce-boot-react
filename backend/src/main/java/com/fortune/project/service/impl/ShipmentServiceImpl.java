package com.fortune.project.service.impl;

import com.fortune.project.entity.AddressEntity;
import com.fortune.project.repository.ShipmentRepository;
import com.fortune.project.service.AddressService;
import com.fortune.project.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final AddressService addressService;
    private final ShipmentRepository shipmentRepository;

    @Override
    public Double calculateShippingFee(Long shippingAddressId, Double subtotal) {
        // FREE SHIP nếu đơn hàng > 500k
        if (subtotal >= 1000000) {
            return 0.0;
        }

        AddressEntity address = addressService.findById(shippingAddressId);

        return (double) (address.getCity().equalsIgnoreCase("HCM") ? 15000 : 30000);
    }
}
