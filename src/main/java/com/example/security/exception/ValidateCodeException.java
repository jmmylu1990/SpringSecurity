package com.example.security.exception;


import org.springframework.security.core.AuthenticationException;

/**
 * 自訂驗證碼校驗錯誤的異常類，繼承 AuthenticationException
 */
public class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String msg, Throwable t) {
        super(msg, t);
    }

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
