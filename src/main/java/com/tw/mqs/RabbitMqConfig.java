package com.tw.mqs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class RabbitMqConfig {
    public static final String EXCHANGE_NAME = "pay-online-refund-exchange";


    public static final String ROUTING_KEY = "pay-online-refund-routing-key";


    public static final String QUEUE_NAME = "pay-online-refund-queue";


    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue payOnlineRefundQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(payOnlineRefundQueue()).to(directExchange()).with(ROUTING_KEY);
    }
}
