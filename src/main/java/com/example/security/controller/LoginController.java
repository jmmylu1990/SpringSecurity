package com.example.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login/page")
    public String loginPage() {  // 获取登录页面
        return "login";
    }
}
