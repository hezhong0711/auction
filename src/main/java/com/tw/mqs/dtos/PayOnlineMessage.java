package com.tw.mqs.dtos;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayOnlineMessage {
    private BigDecimal price;
    private String type;
}
