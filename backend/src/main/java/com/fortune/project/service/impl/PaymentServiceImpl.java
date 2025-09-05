package com.fortune.project.service.impl;

import com.fortune.project.dto.request.order.PaymentResponse;
import com.fortune.project.entity.*;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.PaymentRepository;
import com.fortune.project.service.PaymentService;
import com.fortune.project.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final VnPayService vnPayService;

    @Override
    public PaymentResponse createPayment(OrderEntity order, PaymentMethod method) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmount(order.getTotal());
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        if (method == PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(PaymentStatus.PROCESSING);
            order.setStatus(OrderStatus.PENDING);
            paymentRepository.save(payment);
            return PaymentResponse.from(payment);
        }

        if (method == PaymentMethod.VNPAY) {
            String clientIp = getClientIpAddress();
            String redirectUrl = vnPayService.createPaymentUrl(
                    payment.getId(),
                    payment.getAmount(),
                    clientIp
            );
            return PaymentResponse.from(payment, redirectUrl);
        }

        throw new UnsupportedOperationException("Payment method not supported: " + method);
    }

    @Override
    public void handleGatewayCallback(Long paymentId, boolean success) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "payment id", paymentId));

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.getOrder().setStatus(OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId) {
        return null;
    }

    private String getClientIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}



