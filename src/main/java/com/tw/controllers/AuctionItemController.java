package com.tw.controllers;

import com.tw.controllers.dtos.PayMarginResponse;
import com.tw.services.AuctionService;
import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok(PayMarginResponse.builder().code("SUCCESS").message("支付成功").build());
    }
}
