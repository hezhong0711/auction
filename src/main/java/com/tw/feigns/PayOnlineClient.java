package com.tw.feigns;

import com.tw.feigns.dtos.PayOnlineRequestFeignDto;
import com.tw.feigns.dtos.PayOnlineResponseFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeoutException;

@FeignClient(name = "pay-online", url = "${feign.pay-online.url}")
public interface PayOnlineClient {
    @PostMapping("/3rd-payment/payment")
    PayOnlineResponseFeignDto pay(@RequestBody PayOnlineRequestFeignDto lockSeatRequestFeignDto) throws TimeoutException;
}
