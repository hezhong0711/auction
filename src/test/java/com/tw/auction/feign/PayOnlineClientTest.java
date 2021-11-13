package com.tw.auction.feign;

import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineRequestFeignDto;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PayOnlineClientTest {
    @Test
    void should_return_success_when_call_pay_online_system() throws TimeoutException {
        PayOnlineClient payOnlineClient = Mockito.mock(PayOnlineClient.class);
        when(payOnlineClient.pay(any())).thenReturn(PayOnlineResponseFeignDto
                .builder()
                .code("SUCCESS")
                .message("支付成功")
                .build()
        );
        PayOnlineResponseFeignDto payOnlineResponseFeignDto = payOnlineClient.pay(PayOnlineRequestFeignDto.builder().build());
        assertEquals("SUCCESS", payOnlineResponseFeignDto.getCode());
    }
}
