package com.kee.auth.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kee.auth.handler.CustomLoginAuthenticationProvider;
import com.kee.common.redis.service.RedisService;
import com.kee.common.security.filter.SmsCodeAuthenticationConfig;
import com.kee.common.security.filter.SmsCodeAuthenticationSuccessHandler;
import com.kee.common.security.service.SmsCodeServiceImpl;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;

/**
 * Security 安全认证相关配置
 * Oauth2依赖于Security 默认情况下WebSecurityConfig执行比ResourceServerConfig优先
 * @author zms
 */
@Order(99)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Resource
    private SmsCodeServiceImpl userDetailsService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ClientDetailsService clientDetailsService;

    @Resource
    private RedisService redisService;

    @Lazy
    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        // 使用密码模式进行认证
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        auth.authenticationProvider(new CustomLoginAuthenticationProvider(userDetailsService));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
        .authorizeRequests()
        .antMatchers(
            "/actuator/**",
            "/oauth/**",
            "/token/**","/sms/**").permitAll()
        .anyRequest().authenticated()
                .and().csrf().disable().apply(smsCodeAuthenticationConfig());
    }

    @Bean
    public SmsCodeAuthenticationConfig smsCodeAuthenticationConfig(){
        SmsCodeAuthenticationConfig config =new SmsCodeAuthenticationConfig();
        config.setObjectMapper(objectMapper);
        config.setSmsCodeService(userDetailsService);
        config.setRedisService(redisService);
        config.setSuccessHandler(smsCodeAuthenticationSuccessHandler());
        return config;
    }

    @Bean
    public SmsCodeAuthenticationSuccessHandler smsCodeAuthenticationSuccessHandler(){
        SmsCodeAuthenticationSuccessHandler handler = new SmsCodeAuthenticationSuccessHandler();
        handler.setObjectMapper(objectMapper);
        handler.setClientDetailsService(clientDetailsService);
        handler.setAuthorizationServerTokenServices(authorizationServerTokenServices);
        handler.setPasswordEncoder(passwordEncoder());
        handler.setRedisService(redisService);
        return handler;
    }
}
