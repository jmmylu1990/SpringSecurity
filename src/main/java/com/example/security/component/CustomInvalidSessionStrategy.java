package com.example.security.component;

import com.example.security.pojo.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 使用者請求攜帶無效的 JSESSIONID 存取時的處理策略，即對應的 Session 會話失效
 */
@Component
public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {

    @Autowired
    private ObjectMapper objectMapper;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 清除瀏覽器中的無效的 JSESSIONID
        Cookie cookie = new Cookie("JSESSIONID",null);
        cookie.setPath(getCookiePath(request));
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        String xRequestedWith = request.getHeader("x-requested-with");
        // 判断前端的请求是否为 ajax 请求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 响应 JSON 数据
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResultData<>(1, "SESSION 失效，请重新登录！")));
        }else {
            // 重定向到登录页面
            redirectStrategy.sendRedirect(request, response, "/login/page");
        }
    }

    private String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return contextPath.length() > 0 ? contextPath : "/";
    }
}
