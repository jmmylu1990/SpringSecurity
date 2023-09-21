package com.example.security.component;

import com.example.security.exception.ValidateCodeException;
import com.example.security.pojo.CheckCode;
import com.google.code.kaptcha.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class ImageCodeValidateFilter extends OncePerRequestFilter {

    private String codeParamter = "imageCode";  // 前端輸入的圖形驗證碼參數名

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;  // 自訂認證失敗處理器

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 非 POST 方式的表單提交請求不校驗圖形驗證碼
        if ("/login/form".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            try {
                // 校驗圖形驗證碼合法性
                validate(request);
            } catch (ValidateCodeException e) {
                // 手動擷取圖形驗證碼校驗程序拋出的異常，將其傳給失敗處理器進行處理
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }

        // 放行請求，進入下一個過濾器
        filterChain.doFilter(request, response);
    }

    // 判斷驗證碼的合法性
    private void validate(HttpServletRequest request) {
        // 取得使用者傳入的圖形驗證碼值
        String requestCode = request.getParameter(this.codeParamter);
        if(requestCode == null) {
            requestCode = "";
        }
        requestCode = requestCode.trim();

        // 取得 Session
        HttpSession session = request.getSession();
        // 取得儲存在 Session 裡的驗證碼值
        CheckCode savedCode = (CheckCode) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (savedCode != null) {
            // 隨手清除驗證碼，無論是失敗，或是成功。 用戶端應在登入失敗時刷新驗證碼
            session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
        }

        // 校驗出錯，拋出例外
        if (StringUtils.isBlank(requestCode)) {
            throw new ValidateCodeException("驗證碼的值不能為空");
        }

        if (savedCode == null) {
            throw new ValidateCodeException("驗證碼不存在");
        }

        if (savedCode.isExpried()) {
            throw new ValidateCodeException("驗證碼過期");
        }

        if (!requestCode.equalsIgnoreCase(savedCode.getCode())) {
            throw new ValidateCodeException("驗證碼輸入錯誤");
        }
    }

}
