package com.tw.services;

import com.tw.enums.MarginStatus;
import com.tw.enums.PaymentResult;
import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineRequestFeignDto;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import com.tw.repository.AuctionApply;
import com.tw.repository.AuctionApplyRespository;
import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
public class AuctionService {
    @Autowired
    private AuctionApplyRespository auctionApplyRespository;
    @Autowired
    private PayOnlineClient payOnlineClient;

    public PayMarginResultModel payMargin(PayMarginModel payMarginModel) {
        AuctionApply auctionApply = auctionApplyRespository.findById(payMarginModel.getAuctionItemId())
                .orElseThrow(RuntimeException::new);

        PayOnlineRequestFeignDto payRequest = PayOnlineRequestFeignDto.builder()
                .price(auctionApply.getMarginPrice()).type("wechat").build();
        PayMarginResultModel payMarginResultModel = PayMarginResultModel.builder().build();
        try {
            PayOnlineResponseFeignDto payOnlineResponse = payOnlineClient.pay(payRequest);
            if ("SUCCESS".equals(payOnlineResponse.getCode())) {
                auctionApply.setMarginStatus(MarginStatus.PAY);
                payMarginResultModel.setPaymentResult(PaymentResult.SUCCESS);
            } else if ("NO_ENOUGH_MONEY".equals(payOnlineResponse.getCode())) {
                auctionApply.setMarginStatus(MarginStatus.NOT_PAY);
                payMarginResultModel.setPaymentResult(PaymentResult.FAIL);
            }
            auctionApplyRespository.save(auctionApply);
        } catch (TimeoutException e) {
            e.printStackTrace();
            payMarginResultModel.setPaymentResult(PaymentResult.TIME_OUT);
        }

        return payMarginResultModel;
    }

}
