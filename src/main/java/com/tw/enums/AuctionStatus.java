package com.tw.enums;

public enum AuctionStatus {
    NOT_START("未开始"),
    START("已开始"),
    STOP("已结束");
    private final String description;

    AuctionStatus(String description) {
        this.description = description;
    }
}
