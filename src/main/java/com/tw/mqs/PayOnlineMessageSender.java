package com.tw.mqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.mqs.dtos.PayOnlineMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PayOnlineMessageSender {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean send(PayOnlineMessage payOnlineMessage) {
        try {
            String message = mapper.writeValueAsString(payOnlineMessage);
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, message);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
