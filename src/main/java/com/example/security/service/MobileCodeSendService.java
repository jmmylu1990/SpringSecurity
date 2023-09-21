package com.example.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MobileCodeSendService {
    /**
     * 類比發送手機簡訊驗證碼
     */
    public void send(String mobile, String code) {
        String sendContent = String.format("驗證碼為 %s，請勿洩漏！", code);
        log.info("向手機號碼 " + mobile + " 發送簡訊：" + sendContent);
    }
}
