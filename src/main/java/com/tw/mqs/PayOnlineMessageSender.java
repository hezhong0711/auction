package com.tw.mqs;

import com.tw.mqs.dtos.PayOnlineMessage;
import org.springframework.stereotype.Service;

@Service
public class PayOnlineMessageSender {
    public boolean send(PayOnlineMessage message) {
        return true;
    }
}
