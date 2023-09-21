package com.example.security.service;

import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class MobileUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByMobile(mobile);
        //(1) 從資料庫嘗試讀取該用戶
        if(userOptional.isPresent()){
            User user = userOptional.get();
            //(2) 將資料庫形式的 roles 解析為 UserDetails 的權限集合
            // AuthorityUtils.commaSeparatedStringToAuthorityList() 是 Spring Security 提供的方法，用於將逗號隔開的權限集字串切割為可用權限物件列表
            user.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
            return user;
            // 使用者不存在，拋出異常
        }else {
            log.error("手機號碼尚未註冊，用户不存在");
            throw new UsernameNotFoundException("手機號碼尚未註冊，用户不存在");
        }
    }
}
