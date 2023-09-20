package com.example.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/test")
public class AuthenticationController {

    @GetMapping("/getAuthentication")
    @ResponseBody
    public Object getAuthentication(){
        // 從 SecurityContextHolder 取得認證使用者資訊物件 Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    @GetMapping("/getAuthenticationUserDetails")
    @ResponseBody
    public Object getAuthenticationUserDetails (){
        // 從 SecurityContextHolder 取得認證使用者資訊物件 Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 從 Authentication 中取得 UserDetails
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return user;
    }

    @GetMapping("/getAuthenticationByHttpsesion")
    @ResponseBody
    public Object getAuthenticationByHttpsesion(HttpSession session) {
        // 取得 Session 取得 SecurityContext
        SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        // 從 Authentication 中取得 UserDetails
        UserDetails user = (UserDetails) context.getAuthentication().getPrincipal();
        return user;
    }
}
