package com.example.security.component;

import com.example.security.pojo.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 繼承 SavedRequestAwareAuthenticationSuccessHandler 類，該類別是 failureUrl() 方法使用的驗證失敗處理器
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws ServletException, IOException {
        String xRequestedWith = request.getHeader("x-requested-with");
        // 判斷前端的請求是否為 ajax 請求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 認證失敗，回應 JSON 數據
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResultData<>(1, "驗證失敗!")));
        }else {
            // 以下配置等同於 failureUrl("/login/page?error")
            // 認證失敗後，重定向到指定地址
            // 設定預設的重定的路徑
            super.setDefaultFailureUrl("/login/page?error");
            // 呼叫父類別的 onAuthenticationFailure() 方法
            super.onAuthenticationFailure(request, response, e);
        }
    }
}
