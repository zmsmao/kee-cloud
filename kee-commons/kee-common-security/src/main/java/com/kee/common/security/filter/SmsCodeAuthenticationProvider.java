package com.kee.common.security.filter;

import com.kee.common.security.service.SmsCodeServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Resource;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {


    private SmsCodeServiceImpl userDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        UserDetails userDetails = userDetailService.loadUserByPhone((String) authenticationToken.getPrincipal());
        //授权
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(userDetails,authentication.getCredentials(),userDetails.getAuthorities() );
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public SmsCodeServiceImpl getUserDetailService() {
        return userDetailService;
    }

    public void setUserDetailService(SmsCodeServiceImpl userDetailService) {
        this.userDetailService = userDetailService;
    }
}
