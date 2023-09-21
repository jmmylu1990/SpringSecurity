package com.example.security.service.impl;

import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.service.MobileUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MobileUserServiceImpl implements MobileUserService {


    @Autowired
    private UserRepository userRepository;
    @Override
    public boolean isExistByMobile(String mobile) {

        Optional<User> userOptional = userRepository.findByMobile(mobile);

        return userOptional.isPresent();
    }
}
