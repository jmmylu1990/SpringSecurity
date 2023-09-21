package com.example.security.service;

import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //(1) 從資料庫嘗試讀取該用戶
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            User user = userOptional.get();

            // AuthorityUtils.commaSeparatedStringToAuthorityList() 是 Spring Security 提供的方法，用於將逗號隔開的權限集字串切割為可用權限物件列表
            user.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
            return user;
        }else {
            // 使用者不存在，拋出異常
            throw new UsernameNotFoundException("用户不存在");
        }
    }
}
