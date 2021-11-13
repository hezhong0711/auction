package com.tw.services;

import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    public PayMarginResultModel payMargin(PayMarginModel payMarginModel) {
        return PayMarginResultModel.builder().build();
    }
}
