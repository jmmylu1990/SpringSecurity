package com.example.security.controller;

import com.example.security.pojo.CheckCode;
import com.example.security.pojo.CustomConstants;
import com.example.security.pojo.ResultData;
import com.example.security.service.MobileCodeSendService;
import com.example.security.service.MobileUserService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private MobileCodeSendService mobileCodeSendService;  // 模擬手機簡訊驗證碼發送服務

    @Autowired
    private MobileUserService mobileUserService;

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

    @GetMapping("/mobile/page")
    public String mobileLoginPage() {  // 跳轉到手機簡訊驗證碼登陸頁面
        return "login-mobile";
    }

    @GetMapping("/code/mobile")
    @ResponseBody
    public Object sendMoblieCode(String mobile, HttpServletRequest request) {
        // 隨機產生一個 4 位元的驗證碼
        String code = RandomStringUtils.randomNumeric(4);

        // 將手機驗證碼文字儲存在 Session 中，設定過期時間為 10 * 60s
        CheckCode mobileCode = new CheckCode(code, 10 * 60);
        request.getSession().setAttribute(CustomConstants.MOBILE_SESSION_KEY, mobileCode);

        // 判斷該手機號碼是否註冊
        if(!mobileUserService.isExistByMobile(mobile)) {
            log.info("該手機號碼不存在");
            return new ResultData<>(1, "該手機號碼不存在！");
        }

        // 模擬發送手機簡訊驗證碼到指定用戶手機
        mobileCodeSendService.send(mobile, code);
        log.info("發送成功");
        return new ResultData<>(0, "發送成功！");
    }


}
