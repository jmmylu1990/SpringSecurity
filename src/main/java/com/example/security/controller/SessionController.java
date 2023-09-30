package com.example.security.controller;

import com.example.security.pojo.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SessionController {
    @Autowired
    private SessionRegistry sessionRegistry;



    /*
    * 統計目前使用者未過期的同時 Session 數量
    */
    @GetMapping("/getOnlineSession")
    @ResponseBody
    public Object getOnlineSession() {
        int activeSessionCount = 0;

        for (Object principal : sessionRegistry.getAllPrincipals()) {
            for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                if (session.isExpired()) {
                    sessionRegistry.removeSessionInformation(session.getSessionId());
                } else {
                    activeSessionCount++;
                }
            }
        }
        return new ResultData<>(activeSessionCount);
    }

    /*
     * 統計所有線上用戶
     */
    @GetMapping("/getOnlineUsers")
    @ResponseBody
    public Object getOnlineUsers() {
        // 統計所有線上用戶
        List<String> userList = sessionRegistry.getAllPrincipals().stream()
                .map(user -> ((UserDetails) user).getUsername())
                .collect(Collectors.toList());
        return new ResultData<>(userList);
    }
}
