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
}
