package com.example.security.service;

public interface MobileUserService {

    /**
     * 判斷指定 mobile 是否存在
     */
     boolean isExistByMobile(String mobile);
}
