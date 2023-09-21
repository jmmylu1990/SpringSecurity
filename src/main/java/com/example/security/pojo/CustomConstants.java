package com.example.security.pojo;

import com.google.code.kaptcha.Constants;

public class CustomConstants extends Constants {
    // Session 中儲存驗證碼的 Property
    public static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    // Session 中儲存手機簡訊驗證碼的屬性名
    public static final String MOBILE_SESSION_KEY = "MOBILE_SESSION_KEY";
}
