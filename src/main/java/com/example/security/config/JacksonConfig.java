package com.example.security.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 設定日期轉換
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 設定時區
        // objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // 序列化時，值為 null 的屬性不序列化
        // Include.Include.ALWAYS 默認
        // Include.NON_DEFAULT 屬性為預設值不序列化
        // Include.NON_EMPTY 屬性為空（"" 或 null）都不序列化
        // Include.NON_NULL 屬性為 null 不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 反序列化時，遇到未知屬性的時候不拋出異常
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 序列化成 json 時，將 Long 轉換成 String（防止 js 遺失精確度）
        // Java 的 Long 能表示的範圍比 js 中 number 大，表示部分數值在 js 會變成不準確的值
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
