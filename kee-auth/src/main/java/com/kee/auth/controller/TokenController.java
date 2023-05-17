package com.kee.auth.controller;


import com.kee.api.system.RemoteLogService;
import com.kee.common.core.constant.Constants;
import com.kee.common.core.constant.SecurityConstants;
import com.kee.common.core.domain.R;
import com.kee.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * token 控制
 * 
 * @author zms
 */
@RestController
@RequestMapping("/token")
public class TokenController
{
    @Autowired
    private TokenStore tokenStore;

    @Resource
    private RemoteLogService remoteLogService;

    @DeleteMapping("/logout")
    public R<?> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader)
    {
        if (StringUtils.isEmpty(authHeader))
        {
            return R.ok();
        }

        String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StringUtils.EMPTY).trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        if (accessToken == null || StringUtils.isEmpty(accessToken.getValue()))
        {
            return R.ok();
        }

        // 清空 access token
        tokenStore.removeAccessToken(accessToken);

        // 清空 refresh token
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.removeRefreshToken(refreshToken);
        Map<String, ?> map = accessToken.getAdditionalInformation();
        if (map.containsKey(SecurityConstants.DETAILS_USERNAME))
        {
            String username = (String) map.get(SecurityConstants.DETAILS_USERNAME);
            // 记录用户退出日志
            remoteLogService.saveLogininfor(username, Constants.LOGOUT, "退出成功");
        }
        return R.ok("退出成功");
    }
}
