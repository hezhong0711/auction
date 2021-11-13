package com.tw.enums;

public enum MarginStatus {
    PAY("已支付"),
    NOT_PAY("未支付"),
    REFUND("已退款");
    private final String description;

    MarginStatus(String description) {
        this.description = description;
    }

}
