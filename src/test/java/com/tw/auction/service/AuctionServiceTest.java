package com.tw.auction.service;

import com.tw.auction.base.TestBase;
import com.tw.enums.MarginStatus;
import com.tw.enums.PaymentResult;
import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import com.tw.repository.AuctionApply;
import com.tw.repository.AuctionApplyRespository;
import com.tw.services.AuctionService;
import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
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

    @Test
    public void should_pay_margin_success_given_pay_online_success() throws TimeoutException {
        //stub 依赖 Repository，当调用findById() 方法时返回竞拍申请Entity，当调用 save() 方法时返回更新成功结果；
        // stub 依赖三方支付服务的 FeignClient，当调用微信支付接口时，返回支付成功结果；
        // 实现 Service 缴纳保证金功能，返回缴纳结果为成功
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
}
