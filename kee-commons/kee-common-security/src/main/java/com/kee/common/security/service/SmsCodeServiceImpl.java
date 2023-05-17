package com.kee.common.security.service;

import com.kee.common.security.domain.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public interface SmsCodeServiceImpl extends UserDetailsService{


    /**
     * 使用短信来实现登录
     *
     * @param phone
     * @return
     * @throws UsernameNotFoundException
     */
    UserDetails loadUserByPhone(String phone);


}

