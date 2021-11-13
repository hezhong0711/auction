package com.tw.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.auction.base.TestBase;
import com.tw.enums.PaymentResult;
import com.tw.services.AuctionService;
import com.tw.services.models.PayMarginResultModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuctionItemControllerTest extends TestBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuctionService auctionService;

    @Test
    public void should_change_auction_apply_success_when_pay_margin() throws Exception {

        // given
        long actionItemId = 1L;
        PayMarginResultModel payMarginSuccessModel = PayMarginResultModel.builder()
                .paymentResult(PaymentResult.SUCCESS).build();

        Mockito.when(auctionService.payMargin(any()))
                .thenReturn(payMarginSuccessModel);

        // when
        mockMvc.perform(post("/auction-items/" + actionItemId + "/margin-payment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("支付成功")));
    }

    @Test
    public void should_not_change_auction_apply_when_pay_margin_failed() throws Exception {

        // given
        long actionItemId = 2L;
        PayMarginResultModel payMarginFailedModel = PayMarginResultModel.builder()
                .paymentResult(PaymentResult.FAIL).build();

        Mockito.when(auctionService.payMargin(any()))
                .thenReturn(payMarginFailedModel);

        // when
        mockMvc.perform(post("/auction-items/" + actionItemId + "/margin-payment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("NO_ENOUGH_MONEY")))
                .andExpect(jsonPath("$.message", is("支付失败，余额不足")));
    }

    @Test
    public void should_not_change_auction_apply_when_pay_margin_time_out() throws Exception {

        // given
        long actionItemId = 3L;
        PayMarginResultModel payMarginFailedModel = PayMarginResultModel.builder()
                .paymentResult(PaymentResult.TIME_OUT).build();

        Mockito.when(auctionService.payMargin(any()))
                .thenReturn(payMarginFailedModel);

        // when
        mockMvc.perform(post("/auction-items/" + actionItemId + "/margin-payment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is("PAYMENT_SYSTEM_NOT_AVAILABLE")))
                .andExpect(jsonPath("$.message", is("与支付系统失去联系，请重试")));
    }
}
