package com.example.security.component;

import com.example.security.pojo.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 繼承 SimpleUrlLogoutSuccessHandler 處理器，該類別是 logoutSuccessUrl() 方法使用的成功註銷登入處理器
 */
@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String xRequestedWith = request.getHeader("x-requested-with");
        // 判斷前端的請求是否為 ajax 請求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 成功登出登錄，回應 JSON 數據
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResultData<>(0, "註銷登錄成功！")));
        }else {
            // 以下配置等同於在 http.logout() 後配置 logoutSuccessUrl("/login/page?logout")
            // 設定預設的重定向路徑
            super.setDefaultTargetUrl("/login/page?logout");
            // 呼叫父類別的 onLogoutSuccess() 方法
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}
