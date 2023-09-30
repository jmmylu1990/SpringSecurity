package com.example.security.component;

import com.example.security.pojo.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 前提：Session 並發處理的配置為 maxSessionsPreventsLogin(false)
 * 使用者的同時 Session 會話數量達到上限，新會話登入後，最舊會話會在下一次請求中失效，並執行此策略
 */
@Component
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

    @Autowired
    private ObjectMapper objectMapper;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        // 最舊會話被踢下線時顯示的訊息
        UserDetails userDetails = (UserDetails) event.getSessionInformation().getPrincipal();
        String msg = String.format("用戶[%s]在另外一台機器登錄，您已下線！", userDetails.getUsername());

        String xRequestedWith = event.getRequest().getHeader("x-requested-with");
        // 判斷前端的請求是否為 ajax 請求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 認證成功，回應 JSON 數據
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResultData<>(1, msg)));
        }else {
            // 返回登入頁面顯示訊息
            AuthenticationException e = new AuthenticationServiceException(msg);
            request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", e);
            redirectStrategy.sendRedirect(request, response, "/login/page?error");
        }
    }
}
