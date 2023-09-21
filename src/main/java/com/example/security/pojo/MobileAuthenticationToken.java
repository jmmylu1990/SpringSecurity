package com.example.security.pojo;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MobileAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 520L;
    private final Object principal;

    /**
     * 認證前，使用此構造器進行封裝訊息
     */
    public MobileAuthenticationToken(Object principal) {
        super((Collection) null);     // 使用者權限為 null
        this.principal = principal;   // 前端傳入的手機號
        this.setAuthenticated(false); // 標記為未認證
    }

    /**
     * 認證成功後，使用此構造器封裝使用者訊息
     */
    public MobileAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);          // 使用者權限集合
        this.principal = principal;  // 封裝認證使用者資訊的 UserDetails 對象，不再是手機號
        super.setAuthenticated(true); // 標記認證成功
    }

    @Override
    public Object getCredentials() {
       // 由於使用手機簡訊驗證碼登入不需要密碼，所以直接回傳 null
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        // 手機簡訊驗證碼認證方式不必去除額外的敏感訊息，所以直接呼叫父類方法
        super.eraseCredentials();
    }
}
