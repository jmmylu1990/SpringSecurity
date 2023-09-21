package com.example.security.component;

import com.example.security.pojo.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
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
            //用戶名稱、密碼方式登陸出現異常，需要Redirect到 /login/page?error
            //用手機簡訊驗證碼方式登陸出現認證異常，需要Redirect到 /mobile/?error
            // 使用 Referer 取得目前登入表單提交請求是從哪個登入頁面(/login/page 或 /mobile/page)連結過來的
            String refer = request.getHeader("Referer");
            String lastUrl = StringUtils.substringBefore(refer, "?");
            // 設定預設的Redirect路徑
            super.setDefaultFailureUrl(lastUrl + "?error");
            // 呼叫父類別的 onAuthenticationFailure() 方法
            super.onAuthenticationFailure(request, response, e);
        }
    }
}
