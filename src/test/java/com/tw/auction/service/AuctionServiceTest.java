package com.tw.auction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tw.auction.base.TestBase;
import com.tw.enums.AuctionStatus;
import com.tw.enums.MarginStatus;
import com.tw.enums.PaymentResult;
import com.tw.enums.RefundResult;
import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import com.tw.mqs.PayOnlineMessageSender;
import com.tw.repository.AuctionApply;
import com.tw.repository.AuctionApplyRespository;
import com.tw.repository.PayMessageRepository;
import com.tw.repository.entities.PayMessage;
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
    PayMessageRepository payMessageRepository;

    @MockBean
    PayOnlineClient payOnlineClient;

    @MockBean
    PayOnlineMessageSender payOnlineMessageSender;

    @Test
    public void should_pay_margin_success_given_pay_online_success() throws TimeoutException {
        // given
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.PAY).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineClient.pay(any())).thenReturn(
                PayOnlineResponseFeignDto.builder().code("SUCCESS").message("????????????").build());

        // when
        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(accidentItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);

        // then
        Assertions.assertNotNull(payMarginResultModel);
        Assertions.assertEquals(payMarginResultModel.getPaymentResult(), PaymentResult.SUCCESS);

    }

    @Test
    public void should_pay_margin_failed_given_pay_online_failed() throws TimeoutException {
        // given
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineClient.pay(any())).thenReturn(
                PayOnlineResponseFeignDto.builder().code("NO_ENOUGH_MONEY").message("???????????????????????????").build());

        // when
        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(accidentItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);

        // then
        Assertions.assertNotNull(payMarginResultModel);
        Assertions.assertEquals(payMarginResultModel.getPaymentResult(), PaymentResult.FAIL);

    }

    @Test
    public void should_return_time_out_status_given_pay_online_unavailable() throws TimeoutException {
        // given
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.NOT_PAY).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineClient.pay(any())).thenThrow(TimeoutException.class);

        // when
        PayMarginModel payMarginModel = PayMarginModel.builder().auctionItemId(accidentItemId).build();
        PayMarginResultModel payMarginResultModel = auctionService.payMargin(payMarginModel);

        // then
        Assertions.assertNotNull(payMarginResultModel);
        Assertions.assertEquals(payMarginResultModel.getPaymentResult(), PaymentResult.TIME_OUT);

    }

    @Test
    public void should_refund_margin_success_given_not_during_auction() throws JsonProcessingException {
        // given
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.PAY).auctionStatus(AuctionStatus.NOT_START).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(auctionApplyRespository.save(any())).thenReturn(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.REFUND).accidentItemId(accidentItemId).build());

        Mockito.when(payOnlineMessageSender.send(any())).thenReturn(true);

        // when
        RefundMarginModel refundMarginModel = RefundMarginModel.builder().auctionItemId(accidentItemId).build();
        RefundMarginResultModel refundMarginResultModel = auctionService.refundMargin(refundMarginModel);

        // then
        Assertions.assertNotNull(refundMarginResultModel);
        Assertions.assertEquals(refundMarginResultModel.getRefundResult(), RefundResult.SUCCESS);

    }


    @Test
    public void should_refund_margin_failed_given_during_auction() throws JsonProcessingException {
        // given
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.PAY).auctionStatus(AuctionStatus.START).accidentItemId(accidentItemId)
                .build()));

        // when
        RefundMarginModel refundMarginModel = RefundMarginModel.builder().auctionItemId(accidentItemId).build();
        RefundMarginResultModel refundMarginResultModel = auctionService.refundMargin(refundMarginModel);

        // then
        Assertions.assertNotNull(refundMarginResultModel);
        Assertions.assertEquals(refundMarginResultModel.getRefundResult(), RefundResult.FAIL);

    }

    @Test
    public void should_refund_margin_success_given_not_during_auction_and_pay_online_system_unavailable() throws JsonProcessingException {
        // given
        long accidentItemId = 1L;

        Mockito.when(auctionApplyRespository.findById(any())).thenReturn(Optional.of(AuctionApply.builder()
                .id(1L).marginStatus(MarginStatus.PAY).auctionStatus(AuctionStatus.NOT_START).accidentItemId(accidentItemId)
                .build()));

        Mockito.when(payMessageRepository.saveAndFlush(any())).thenReturn(PayMessage.builder().content("1").build());

        Mockito.when(payOnlineMessageSender.send(any())).thenReturn(false);

        // when
        RefundMarginModel refundMarginModel = RefundMarginModel.builder().auctionItemId(accidentItemId).build();
        RefundMarginResultModel refundMarginResultModel = auctionService.refundMargin(refundMarginModel);

        // then
        Assertions.assertNotNull(refundMarginResultModel);
        Assertions.assertEquals(refundMarginResultModel.getRefundResult(), RefundResult.SUCCESS);

    }
}
