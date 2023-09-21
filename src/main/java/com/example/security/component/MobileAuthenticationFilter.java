package com.example.security.component;

import com.example.security.pojo.MobileAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 手機簡訊驗證碼認證過濾器，仿照 UsernamePasswordAuthenticationFilter 過濾器編寫
 */
@Slf4j
public class MobileAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String mobileParamter = "mobile";  // 預設手機號參數名為 mobile
    private boolean postOnly = true;    // 預設請求方式只能為 POST

    protected MobileAuthenticationFilter() {
        // 預設登入表單提交路徑為 /mobile/form，POST 方式請求
        super(new AntPathRequestMatcher("/mobile/form", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
       log.info("MobileAuthenticationFilter.attemptAuthentication()");
        //(1) 預設情況下，如果請求方式不是 POST，會拋出異常
        if(postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }else {
            //(2) 取得請求攜帶的 mobile
            String mobile = request.getParameter(mobileParamter);
            if(mobile == null) {
                mobile = "";
            }
            mobile = mobile.trim();

            //(3) 使用前端傳入的 mobile 建構 Authentication 物件，標記該對象未認證
            // MobileAuthenticationToken 是我們自訂的 Authentication 類
            MobileAuthenticationToken authRequest = new MobileAuthenticationToken(mobile);
            //(4) 將請求中的一些屬性資訊設定到 Authentication 物件中，如：remoteAddress，sessionId
            this.setDetails(request, authRequest);
            //(5) 呼叫 ProviderManager 類別的 authenticate() 方法進行身份認證
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Nullable
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(this.mobileParamter);
    }

    protected void setDetails(HttpServletRequest request, MobileAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setMobileParameter(String mobileParamter) {
        Assert.hasText(mobileParamter, "Mobile par ameter must not be empty or null");
        this.mobileParamter = mobileParamter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public String getMobileParameter() {
        return mobileParamter;
    }


}
