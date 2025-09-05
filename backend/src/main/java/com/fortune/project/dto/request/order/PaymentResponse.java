package com.fortune.project.dto.request.order;

import com.fortune.project.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long id;
    private String method;
    private String status;
    private Double amount;
    private String transactionId;
    private String gatewayRedirectUrl;

    public static PaymentResponse from(PaymentEntity payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getTransactionId(),
                null
        );
    }

    public static PaymentResponse from(PaymentEntity payment, String redirectUrl) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getTransactionId(),
                redirectUrl
        );
    }
}

