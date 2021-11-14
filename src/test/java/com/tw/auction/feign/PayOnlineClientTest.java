package com.tw.auction.feign;

import com.tw.auction.base.TestBase;
import com.tw.feigns.PayOnlineClient;
import com.tw.feigns.dtos.PayOnlineRequestFeignDto;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.concurrent.TimeoutException;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class PayOnlineClientTest extends TestBase {
    @Autowired
    private PayOnlineClient payOnlineClient;

    private ClientAndServer mockServer;

    @BeforeEach
    public void startMockServer() {
        mockServer = startClientAndServer(8082);
    }

    @AfterEach
    public void stopMockServer() {
        mockServer.stop();
    }

    @Test
    void should_return_success_when_call_pay_online_system() throws TimeoutException {
        // given
        mockServer.when(
                request().withPath("/3rd-payment/payment")
        ).respond(
                response().withHeaders(
                        new Header(CONTENT_TYPE.toString(), MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
                ).withBody("{\"code\": \"SUCCESS\",\"message\":\"支付成功\"}")
        );

        // when
        PayOnlineResponseFeignDto responseFeignDto = payOnlineClient.pay(PayOnlineRequestFeignDto.builder().build());

        // then
        assertEquals("SUCCESS", responseFeignDto.getCode());
        assertEquals("支付成功", responseFeignDto.getMessage());
    }


    @Test
    void should_return_fail_when_pay_online_system_return_fail() throws TimeoutException {
        // given
        mockServer.when(
                request().withPath("/3rd-payment/payment")
        ).respond(
                response().withHeaders(
                                new Header(CONTENT_TYPE.toString(), MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
                        )
                        .withBody("{\"code\": \"NO_ENOUGH_MONEY\",\"message\":\"支付失败，余额不足\"}")
        );

        // when
        PayOnlineResponseFeignDto responseFeignDto = payOnlineClient.pay(PayOnlineRequestFeignDto.builder().build());

        // then
        assertEquals("NO_ENOUGH_MONEY", responseFeignDto.getCode());
        assertEquals("支付失败，余额不足", responseFeignDto.getMessage());
    }

    @Test
    void should_throw_timeout_exception_when_pay_online_system_unavailable() {
        // given
        mockServer.when(
                request().withPath("/3rd-payment/payment")
        ).respond(
                response().withHeaders(
                                new Header(CONTENT_TYPE.toString(), MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
                        ).withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR_500.code())
                        .withBody("{\"code\": \"NO_ENOUGH_MONEY\",\"message\":\"支付失败，余额不足\"}")
        );

        // when
        Exception exception = assertThrows(TimeoutException.class, () -> payOnlineClient.pay(PayOnlineRequestFeignDto.builder().build()),
                "Expected doThing() to throw, but it didn't");

        // then
        assertTrue(exception.getMessage().contains("服务不可用"));

    }
}
