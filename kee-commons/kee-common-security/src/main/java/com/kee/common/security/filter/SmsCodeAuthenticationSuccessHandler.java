package com.kee.common.security.filter;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.redis.service.RedisService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Getter
@Setter
public class SmsCodeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ClientDetailsService clientDetailsService;

    private AuthorizationServerTokenServices authorizationServerTokenServices;

    private ObjectMapper objectMapper;

    private PasswordEncoder passwordEncoder;

    private RedisService redisService;

    public static final String PHONE_KEY = "phone";

    public static final String SMS_CODE_KEY  = "code";

    public static final String PATH_URL = "/sms/login";

    private final static String SMS_PHONE="sms:phone:";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            // 1. 从请求头中获取 ClientId
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Basic ")) {
                throw new UnapprovedClientAuthenticationException("请求头中无client信息");
            }
            String[] tokens = extractAndDecodeHeader(header, request);
            String clientId = tokens[0];
            String clientSecret = tokens[1];
            //2. 根据clientDetailsService获取clientDetails
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            // 3. 校验 ClientId和 ClientSecret的正确性
            if (clientDetails == null) {
                //删除验证码
                redisService.deleteObject(SMS_PHONE + obtainPhone(request));
                throw new UnapprovedClientAuthenticationException("clientId:" + clientId + "对应的信息不存在");
            } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
                //删除验证码
                redisService.deleteObject(SMS_PHONE + obtainPhone(request));
                throw new UnapprovedClientAuthenticationException("clientSecret不正确");
            }
            //4. 组建tokenQuest
            TokenRequest tokenRequest = new TokenRequest(MapUtil.empty(), clientId, clientDetails.getScope(), "sms");

            //5. 组建OAuth2
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

            //6.根据Authentication和OAuth2Request 组建 OAuth2Authentication
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

            //通过authorizationServerTokenServices 来 获取 accessToken
            OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            //删除验证码
            redisService.deleteObject(SMS_PHONE + obtainPhone(request));
            log.info("登录成功");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(accessToken));
    }

    private static String[] extractAndDecodeHeader(String header, HttpServletRequest request) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
    }

    @Nullable
    private String obtainPhone(HttpServletRequest request) {
        return request.getParameter(PHONE_KEY);
    }

    @Nullable
    private String obtainCode(HttpServletRequest request) {
        return request.getParameter(SMS_CODE_KEY);
    }

}
