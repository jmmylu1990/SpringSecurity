package com.example.security.config;

import com.example.security.pojo.CustomConstants;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha captchaProducer() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有邊框
        properties.setProperty(CustomConstants.KAPTCHA_BORDER, "yes");
        // 邊框顏色
        properties.setProperty(CustomConstants.KAPTCHA_BORDER_COLOR, "192,192,192");
        // 驗證碼圖片的寬和高
        properties.setProperty(CustomConstants.KAPTCHA_IMAGE_WIDTH, "110");
        properties.setProperty(CustomConstants.KAPTCHA_IMAGE_HEIGHT, "40");
        // 驗證碼顏色
        properties.setProperty(CustomConstants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "0,0,0");
        // 驗證碼字型大小
        properties.setProperty(CustomConstants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "32");
        // 驗證碼產生幾個字元
        properties.setProperty(CustomConstants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        // 驗證碼隨機字元庫
        properties.setProperty(CustomConstants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
        // 驗證碼圖片預設是有線條干擾的，設定成沒有干擾
        properties.setProperty(CustomConstants.KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
