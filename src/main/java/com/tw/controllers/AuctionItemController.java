package com.tw.controllers;

import com.tw.controllers.dtos.PayMarginResponse;
import com.tw.enums.PaymentResult;
import com.tw.services.AuctionService;
import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auction-items")
public class AuctionItemController {
    @Autowired
    AuctionService auctionService;

    @PostMapping("/{aid}/margin-payment")
    public ResponseEntity<PayMarginResponse> payMargin(@PathVariable("aid") Long auctionItemId) {
        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(auctionItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);
        PayMarginResponse payMarginResponse = PayMarginResponse.builder().build();
        if (payMarginResultModel.getPaymentResult().equals(PaymentResult.FAIL)) {
            payMarginResponse.setCode("NO_ENOUGH_MONEY");
            payMarginResponse.setMessage("支付失败，余额不足");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(payMarginResponse);
        } else if (payMarginResultModel.getPaymentResult().equals(PaymentResult.TIME_OUT)) {
            payMarginResponse.setCode("PAYMENT_SYSTEM_NOT_AVAILABLE");
            payMarginResponse.setMessage("与支付系统失去联系，请重试");
            return ResponseEntity.internalServerError().body(payMarginResponse);
        }

        payMarginResponse.setCode("SUCCESS");
        payMarginResponse.setMessage("支付成功");
        return ResponseEntity.ok(payMarginResponse);
    }
}
