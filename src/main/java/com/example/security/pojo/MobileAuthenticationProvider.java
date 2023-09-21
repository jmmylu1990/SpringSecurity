package com.example.security.pojo;

import com.example.security.service.MobileUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

public class MobileAuthenticationProvider implements AuthenticationProvider {

    private MobileUserDetailsService mobileUserDetailsService;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserDetailsChecker authenticationChecks = new MobileAuthenticationProvider.DefaultAuthenticationChecks();

    /**
     * 處理認證
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //(1) 如果入參的 Authentication 類型不是 MobileAuthenticationToken，則拋出例外
        Assert.isInstanceOf(MobileAuthenticationToken.class, authentication, () -> {
            return this.messages.getMessage("MobileAuthenticationProvider.onlySupports", "Only MobileAuthenticationToken is supported");
        });
        // 取得手機號碼
        String mobile = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
        //(2) 根據手機號碼從資料庫中查詢用戶信息
        UserDetails user = this.mobileUserDetailsService.loadUserByMobile(mobile);

        //(4) 檢查帳號是否鎖定、帳號是否可用、帳號是否過期、密碼是否過期
        this.authenticationChecks.check(user);

        //(5) 查詢到了用戶信息，則認證通過，構建標記認證成功用戶信息類對象 AuthenticationToken
        MobileAuthenticationToken result = new MobileAuthenticationToken(user, user.getAuthorities());
        // 需要把認證前 Authentication 物件中的 details 資訊加入認證後的 Authentication
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * ProviderManager 管理員透過此方法來判斷是否採用此 AuthenticationProvider 類別
     * 來處理由 AuthenticationFilter 過濾器傳入的 Authentication 對象
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // isAssignableFrom 傳回 true 當且僅當呼叫者為父類別.class，參數為本身或其子類別.class
        // ProviderManager 會取得 MobileAuthenticationFilter 過濾器傳入的 Authentication 類型
        // 所以當且僅當 authentication 的類型為 MobileAuthenticationToken 才會傳回 true
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 此處傳入自訂的 MobileUserDetailsSevice 對象
     */
    public void setUserDetailsService(MobileUserDetailsService mobileUserDetailsService) {
        this.mobileUserDetailsService = mobileUserDetailsService;
    }

    public MobileUserDetailsService getUserDetailsService() {
        return mobileUserDetailsService;
    }

    /**
     * 檢查帳號是否鎖定、帳號是否可用、帳號是否過期、密碼是否過期
     */
    private class DefaultAuthenticationChecks implements UserDetailsChecker {
        private DefaultAuthenticationChecks() {
        }

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                throw new LockedException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
            } else if (!user.isEnabled()) {
                throw new DisabledException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
            } else if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            } else if (!user.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
            }
        }
    }
}
