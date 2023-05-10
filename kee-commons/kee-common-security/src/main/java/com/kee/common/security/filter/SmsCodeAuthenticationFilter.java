package com.kee.common.security.filter;

import com.alibaba.fastjson.JSON;
import com.kee.common.core.domain.SmsCode;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.redis.service.RedisService;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String PHONE_KEY = "phone";

    public static final String SMS_CODE_KEY  = "code";

    public static final String PATH_URL = "/sms/login";

    public boolean postOnly = true;

    private final static String SMS_PHONE="sms:phone:";

    private RedisService redisService;



    public SmsCodeAuthenticationFilter(){
        super(new AntPathRequestMatcher(PATH_URL,"POST"));
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        Map<String,String> map = new HashMap<>();
        String phone = obtainPhone(request);
        String code = obtainCode(request);
        map.put(PHONE_KEY,phone);
        map.put(SMS_CODE_KEY,code);
        validateSmsCode(map);
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(phone,code);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Nullable
    protected String obtainPhone(HttpServletRequest request) {
        return request.getParameter(PHONE_KEY);
    }

    @Nullable
    protected String obtainCode(HttpServletRequest request) {
        return request.getParameter(SMS_CODE_KEY);
    }


    private void validateSmsCode(Map<String,String> map){
        String smsCode = map.get("code");
        String phone = map.get("phone");
        if(StringUtils.isNull(phone)){
            throw new  RuntimeException("请求体无phone参数");
        }
        if (StringUtils.isNull(smsCode))
        {
            throw new  RuntimeException("请求体无code参数");
        }
        if (StringUtils.isEmpty(phone)) {
            throw new RuntimeException("手机号码不能为空！");
        }
        if (StringUtils.isEmpty(smsCode)) {
            throw new RuntimeException("验证码不能为空！");
        }
        SmsCode cacheObject = redisService.getCacheObject(SMS_PHONE + phone);
        if(StringUtils.isNull(cacheObject))
        {
            redisService.deleteObject(SMS_PHONE + phone);
            throw new RuntimeException("验证码不存在，请重新发送！");
        }
        if(cacheObject.isExpire())
        {
            redisService.deleteObject(SMS_PHONE + phone);
            throw new RuntimeException("验证码已过期，请重新发送！");
        }
        if (!cacheObject.getCode().equals(smsCode)) {
            redisService.deleteObject(SMS_PHONE + phone);
            throw new RuntimeException("验证码不正确，请重新发送！");
        }
        if (!cacheObject.getPhone().equals(phone)) {
            redisService.deleteObject(SMS_PHONE + phone);
            throw new RuntimeException("手机号码不正确，请重新发送！");
        }
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public RedisService getRedisService() {
        return redisService;
    }
}
