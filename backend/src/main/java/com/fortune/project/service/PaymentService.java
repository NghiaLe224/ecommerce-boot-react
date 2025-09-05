package com.fortune.project.service;

import com.fortune.project.dto.request.order.PaymentResponse;
import com.fortune.project.entity.OrderEntity;
import com.fortune.project.entity.PaymentMethod;

public interface PaymentService {
    PaymentResponse createPayment(OrderEntity order, PaymentMethod method);
    void handleGatewayCallback(Long paymentId, boolean success);

    PaymentResponse refundPayment(Long paymentId);
}
