package com.tw.auction.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.mqs.PayOnlineMessageSender;
import com.tw.mqs.dtos.PayOnlineMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PayOnlineMessageSenderTest {
    @Test
    public void should_return_true_given_send_message_success() {
        RabbitTemplate stubRabbitTemplate = Mockito.mock(RabbitTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();
        PayOnlineMessageSender payOnlineMessageSender = new PayOnlineMessageSender(objectMapper, stubRabbitTemplate);
        boolean sendMessageResult = payOnlineMessageSender.send(PayOnlineMessage.builder()
                .type("wechat")
                .price(BigDecimal.valueOf(2000L))
                .build());
        assertTrue(sendMessageResult);
    }
}
