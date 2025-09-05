package com.fortune.project.service;

import java.math.BigDecimal;
import java.util.Map;

public interface VnPayService {
    String createPaymentUrl(Long paymentId, Double amount, String clientIp);
    boolean validateReturn(Map<String, String> vnpParams, String receivedHash);
}
