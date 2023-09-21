package com.example.security.controller;

import com.example.security.pojo.CheckCode;
import com.example.security.pojo.CustomConstants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @GetMapping("/login/page")
    public String loginPage() {  // 获取登录页面
        return "login";
    }

    @GetMapping("/code/image")
    public void imageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 建立驗證碼文字
        String capText = defaultKaptcha.createText();
        // 建立驗證碼圖片
        BufferedImage image = defaultKaptcha.createImage(capText);

        // 將驗證碼文字放進 Session 中
        CheckCode code = new CheckCode(capText);
        request.getSession().setAttribute(CustomConstants.KAPTCHA_SESSION_KEY, code);

        // 將驗證碼圖片返回，禁止驗證碼圖片快取
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpg", response.getOutputStream());
    }


}
