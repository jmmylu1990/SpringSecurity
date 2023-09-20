package com.example.security.component;

import com.example.security.pojo.ResultData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 繼承 SavedRequestAwareAuthenticationSuccessHandler 類，該類別是 defaultSuccessUrl() 方法使用的認證成功處理器
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String xRequestedWith = request.getHeader("x-requested-with");
        // 判斷前端的請求是否為 ajax 請求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 認證成功，回應 JSON 數據
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResultData<>(0, "認證成功!")));
        }else {
            // 以下配置等同於 defaultSuccessUrl("/index")

            // 認證成功後，如果存在原始存取路徑，則重定向到該路徑；如果沒有，則重定向 /index
            // 設定預設的重定的路徑
            super.setDefaultTargetUrl("/index");
            // 呼叫父類別的 onAuthenticationSuccess() 方法
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
