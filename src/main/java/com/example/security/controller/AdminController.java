package com.example.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// 只能擁有 ROLE_ADMIN 權限的使用者訪問
@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello，admin！！！";
    }
}
