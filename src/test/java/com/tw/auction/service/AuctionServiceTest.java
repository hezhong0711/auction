package com.tw.auction.service;

import com.tw.auction.base.TestBase;
import com.tw.enums.MarginStatus;
import com.tw.enums.PaymentResult;
import com.tw.enums.RefundResult;
import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import com.tw.mqs.PayOnlineMessageSender;
import com.tw.repository.AuctionApply;
import com.tw.repository.AuctionApplyRespository;
import com.tw.services.AuctionService;
import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
import com.tw.services.models.RefundMarginModel;
import com.tw.services.models.RefundMarginResultModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;

public class AuctionServiceTest extends TestBase {
    @Autowired
    AuctionService auctionService;

    @MockBean
    AuctionApplyRespository auctionApplyRespository;

    @MockBean
    PayOnlineClient payOnlineClient;

    @MockBean
    PayOnlineMessageSender payOnlineMessageSender;

    @Test
    public void should_pay_margin_success_given_pay_online_success() throws TimeoutException {
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.PAY).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineClient.pay(any())).thenReturn(
                PayOnlineResponseFeignDto.builder().code("SUCCESS").message("支付成功").build());

        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(accidentItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);

        Assertions.assertNotNull(payMarginResultModel);
        Assertions.assertEquals(payMarginResultModel.getPaymentResult(), PaymentResult.SUCCESS);

    }

    @Test
    public void should_pay_margin_failed_given_pay_online_failed() throws TimeoutException {
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineClient.pay(any())).thenReturn(
                PayOnlineResponseFeignDto.builder().code("NO_ENOUGH_MONEY").message("支付失败，余额不足").build());

        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(accidentItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);

        Assertions.assertNotNull(payMarginResultModel);
        Assertions.assertEquals(payMarginResultModel.getPaymentResult(), PaymentResult.FAIL);

    }

    @Test
    public void should_return_time_out_status_given_pay_online_unavailable() throws TimeoutException {
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineClient.pay(any())).thenThrow(TimeoutException.class);

        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(accidentItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);

        Assertions.assertNotNull(payMarginResultModel);
        Assertions.assertEquals(payMarginResultModel.getPaymentResult(), PaymentResult.TIME_OUT);

    }

    @Test
    public void should_refund_margin_success_given_not_during_auction() throws TimeoutException {
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.REFUND).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineMessageSender.send(any())).thenReturn(true);

        RefundMarginModel refundMarginModel = RefundMarginModel.builder().auctionItemId(accidentItemId).build();
        RefundMarginResultModel refundMarginResultModel = auctionService.refundMargin(refundMarginModel);

        Assertions.assertNotNull(refundMarginResultModel);
        Assertions.assertEquals(refundMarginResultModel.getRefundResult(), RefundResult.SUCCESS);

    }
}
