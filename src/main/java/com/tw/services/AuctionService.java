package com.tw.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.enums.AuctionStatus;
import com.tw.enums.MarginStatus;
import com.tw.enums.PaymentResult;
import com.tw.enums.RefundResult;
import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineRequestFeignDto;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import com.tw.mqs.PayOnlineMessageSender;
import com.tw.mqs.dtos.PayOnlineMessage;
import com.tw.repository.AuctionApply;
import com.tw.repository.AuctionApplyRespository;
import com.tw.repository.PayMessageRepository;
import com.tw.repository.entities.PayMessage;
import com.tw.services.models.PayMarginModel;
import com.tw.services.models.PayMarginResultModel;
import com.tw.services.models.RefundMarginModel;
import com.tw.services.models.RefundMarginResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
public class AuctionService {
    @Autowired
    private AuctionApplyRespository auctionApplyRespository;
    @Autowired
    private PayOnlineClient payOnlineClient;
    @Autowired
    private PayOnlineMessageSender payOnlineMessageSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PayMessageRepository payMessageRepository;

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

    public RefundMarginResultModel refundMargin(RefundMarginModel refundMarginModel) throws JsonProcessingException {
        AuctionApply auctionApply = auctionApplyRespository.findById(refundMarginModel.getAuctionItemId())
                .orElseThrow(RuntimeException::new);

        if (auctionApply.getAuctionStatus().equals(AuctionStatus.NOT_START)) {
            PayOnlineMessage payOnlineMessage = PayOnlineMessage.builder()
                    .type("wechat").price(auctionApply.getMarginPrice()).build();
            boolean mqResult = payOnlineMessageSender.send(payOnlineMessage);
            if (!mqResult) {
                saveMessage(payOnlineMessage);
            }
            return RefundMarginResultModel.builder().refundResult(RefundResult.SUCCESS).build();
        } else if (auctionApply.getAuctionStatus().equals(AuctionStatus.START)) {
            return RefundMarginResultModel.builder().refundResult(RefundResult.FAIL).build();
        }

        return RefundMarginResultModel.builder().build();
    }

    private void saveMessage(PayOnlineMessage payOnlineMessage) throws JsonProcessingException {

        String content = objectMapper.writeValueAsString(payOnlineMessage);
        PayMessage messageEntity = PayMessage.builder()
                .content(content)
                .build();
        payMessageRepository.saveAndFlush(messageEntity);
    }
}
