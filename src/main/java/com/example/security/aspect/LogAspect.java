package com.example.security.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class LogAspect {
    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("execution(* com.example.security.service..*(..))")
    public void pointcut() {
    }
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        String className = joinPoint.getTarget().getClass().getName(); // 取得切入點的類別名稱
        String methodName = joinPoint.getSignature().getName();        // 取得切入點的方法名稱

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Parameter[] parameters = signature.getMethod().getParameters(); // 取得方法輸入參數資訊
        Object[] args = joinPoint.getArgs(); // 取得輸入參數值
        String paramsMapJsonString = getParamsMapJsonString(parameters, args);
        log.info(paramsMapJsonString);
    }

    private String getParamsMapJsonString(Parameter[] parameters, Object[] args) {
        Map<String, Object> params = new HashMap<>();
        for (int i = 0 ; i < parameters.length; i++) {
            params.put(parameters[i].getName(), args[i]);
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params);
        } catch (JsonProcessingException e) {
            log.error("parse args to json error, args={}", args);
            return "";
        }
    }
}
