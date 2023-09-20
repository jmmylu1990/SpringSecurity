package com.example.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping({"/", "/index"})
    @ResponseBody
    public String index() {   // 跳转到主页
        return "--------------歡迎登錄--------------";
    }
}
