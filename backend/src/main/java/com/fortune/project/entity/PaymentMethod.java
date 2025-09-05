package com.fortune.project.entity;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH_ON_DELIVERY("Cash On Delivery"),
    VNPAY("VNPay"),
    MOMO("Momo");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

}
