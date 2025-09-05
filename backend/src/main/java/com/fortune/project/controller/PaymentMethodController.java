package com.fortune.project.controller;

import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.entity.PaymentMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentMethodController {

    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
         List<String> response = Arrays.stream(PaymentMethod.values())
                .map(PaymentMethod::getDisplayName)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>("Fetched all payment method successfully", response, LocalDateTime.now()));
    }
}
