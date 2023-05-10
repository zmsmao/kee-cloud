package com.kee.common.security.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kee.common.redis.service.RedisService;
import com.kee.common.security.service.SmsCodeServiceImpl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Getter
@Setter
public class SmsCodeAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>  {

    private ObjectMapper objectMapper;

    private SmsCodeServiceImpl smsCodeService;

    private SmsCodeAuthenticationSuccessHandler successHandler;

    private RedisService redisService;


    @Override
    public void configure(HttpSecurity builder){
        SmsCodeAuthenticationFilter filter = new SmsCodeAuthenticationFilter();
        filter.setRedisService(redisService);
        filter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(successHandler);
        SmsCodeAuthenticationProvider provider = new SmsCodeAuthenticationProvider();
        provider.setUserDetailService(smsCodeService);
        builder.authenticationProvider(provider)
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
