package com.kee.auth.handler;


import com.kee.api.system.RemoteLogService;
import com.kee.common.core.constant.Constants;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.redis.service.RedisService;
import com.kee.common.security.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 认证成功处理
 *
 * @author zms
 */
@Component
public class AuthenticationSuccessEventHandler implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private RemoteLogService remoteLogService;


    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = (Authentication) event.getSource();
        if (StringUtils.isNotEmpty(authentication.getAuthorities())
                && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser user = (LoginUser) authentication.getPrincipal();
            String username = user.getUsername();
            // 记录用户登录日志
            remoteLogService.saveLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        }
    }
}
