package com.example.security.component;

import com.example.security.pojo.MobileAuthenticationProvider;
import com.example.security.service.MobileUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

@Component
public class MobileAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;  // 自訂認證成功處理器

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;  // 自訂認證失敗處理器

    @Autowired
    private MobileCodeValidateFilter mobileCodeValidaterFilter;  // 手機簡訊驗證碼校驗過濾器

    @Autowired
    private MobileUserDetailsService mobileUserDetailsService; // 手機簡訊驗證方式的 UserDetail

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //(1) 將簡訊驗證碼認證的自訂過濾器綁定到 HttpSecurity 中
        //(1.1) 建立手機簡訊驗證碼認證過濾器的實例 filer
        MobileAuthenticationFilter filter = new MobileAuthenticationFilter();

        //(1.2) 設定 filter 使用 AuthenticationManager(ProviderManager 介面實作類別) 認證管理器
        // 多種登入方式應該使用同一個認證管理器實例，所以取得 Spring 容器中已經存在的 AuthenticationManager 實例
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        filter.setAuthenticationManager(authenticationManager);

        //(1.3) 設定 filter 使用自訂成功和失敗處理器
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);

        //(1.4) 設定 filter 使用 SessionAuthenticationStrategy 會話管理器
        // 多種登入方式應該使用同一個會話管理器實例，取得 Spring 容器已經存在的 SessionAuthenticationStrategy 實例
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);

        //(1.5) 在 UsernamePasswordAuthenticationFilter 過濾器之前新增 MobileCodeValidateFilter 過濾器
        // 在 UsernamePasswordAuthenticationFilter 過濾器之後新增 MobileAuthenticationFilter 過濾器
        http.addFilterBefore(mobileCodeValidaterFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);

        //(2) 將自訂的 MobileAuthenticationProvider 處理器綁定到 HttpSecurity 中
        //(2.1) 建立手機簡訊驗證碼認證過濾器的 AuthenticationProvider 實例，並指定所使用的 UserDetailsService
        MobileAuthenticationProvider provider = new MobileAuthenticationProvider();
        provider.setUserDetailsService(mobileUserDetailsService);

        //(2.2) 將該 AuthenticationProvider 實例綁定到 HttpSecurity 中
        http.authenticationProvider(provider);
    }

}
