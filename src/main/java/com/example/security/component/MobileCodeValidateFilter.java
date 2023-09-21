package com.example.security.component;

import com.example.security.exception.ValidateCodeException;
import com.example.security.pojo.CheckCode;
import com.example.security.pojo.CustomConstants;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 手機簡訊驗證碼校驗
 */
@Slf4j
@Component
public class MobileCodeValidateFilter extends OncePerRequestFilter {

    private String codeParamter = "mobileCode";  // 前端輸入的手機簡訊驗證碼參數名

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler; // 自訂認證失敗處理器

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      log.info("MobileCodeValidateFilter.doFilterInternal()");
        // 非 POST 方式的手機簡訊驗證碼提交請求不進行校驗
        if("/mobile/form".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            try {
                // 檢驗手機驗證碼的合法性
                validate(request);
            } catch (ValidateCodeException e) {
                // 將異常交給自訂失敗處理器進行處理
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }

        // 放行，進入下一個過濾器
        filterChain.doFilter(request, response);
    }

    /**
     * 檢驗用戶輸入的手機驗證碼的合法性
     */
    private void validate(HttpServletRequest request) {
        // 取得用戶傳入的手機驗證碼值
        String requestCode = request.getParameter(this.codeParamter);
        if(requestCode == null) {
            requestCode = "";
        }
        requestCode = requestCode.trim();


        // 獲取 Session
        HttpSession session = request.getSession();
        // 取得 Session 中儲存的手機簡訊驗證碼
        CheckCode savedCode = (CheckCode) session.getAttribute(CustomConstants.MOBILE_SESSION_KEY);

        if (savedCode != null) {
            // 隨手清除驗證碼，無論是失敗，還是成功。 用戶端應在登入失敗時刷新驗證碼
            session.removeAttribute(CustomConstants.MOBILE_SESSION_KEY);
        }

        // 隨手清除驗證碼，無論是失敗，還是成功。 用戶端應在登入失敗時刷新驗證碼
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
