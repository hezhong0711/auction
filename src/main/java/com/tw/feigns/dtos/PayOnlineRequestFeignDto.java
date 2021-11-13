package com.tw.feigns.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayOnlineRequestFeignDto {
    private BigDecimal price;
    private String type;
}
