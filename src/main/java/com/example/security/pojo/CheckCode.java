package com.example.security.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CheckCode implements Serializable {

    private static final long serialVersionUID = -5069182134966515799L;

    public CheckCode() {
    }

    private String code;           // 驗證碼字元
    private LocalDateTime expireTime;  // 過期時間

    /**
     * @param code 驗證碼字元
     * @param expireTime 過期時間，單位秒
     */
    public CheckCode(String code, int expireTime) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireTime);
    }

    public CheckCode(String code) {
        // 預設驗證碼 60 秒後過期
        this(code, 60);
    }

    // 是否過期
    public boolean isExpried() {
        return this.expireTime.isBefore(LocalDateTime.now());
    }

    public String getCode() {
        return this.code;
    }
}
