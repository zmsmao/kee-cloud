package com.kee.auth.handler;


import com.kee.common.core.constant.Constants;
import com.kee.common.redis.service.RedisService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.TimeUnit;


public class CustomLoginAuthenticationProvider extends DaoAuthenticationProvider {


    private final RedisService redisService;

    public CustomLoginAuthenticationProvider(UserDetailsService userDetailsService, RedisService redisService) {
        super();
        this.redisService = redisService;
        // 这个地方一定要对userDetailsService赋值，不然userDetailsService是null
        setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (Constants.CUSTOM_LOGIN_SMS.equals(presentedPassword)) {
                //免密登录，不验证密码（还可以继续扩展，只要传进来的password标识即可）
            } else {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                    //记录登录失败的次数
//                    loginFailure(userDetails.getUsername());
                    this.logger.debug("Authentication failed: password does not match stored value");
                    throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
                }
            }
        }
    }

//    private void loginFailure(String username) {
//        String key = Constants.PWD_ERR_CNT_KEY + username;
//        Integer retryCount = redisService.getCacheObject(key);
//        redisService.setCacheObject(key, retryCount == null ? 1 : retryCount + 1, Constants.PASSWORD_LOCK_TIME, TimeUnit.MINUTES);
//    }

}

