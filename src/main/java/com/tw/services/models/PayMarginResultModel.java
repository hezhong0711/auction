package com.tw.services.models;

import com.tw.enums.PaymentResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayMarginResultModel {
    private PaymentResult paymentResult;
}
