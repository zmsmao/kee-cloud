package com.kee.common.security.service;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Date;

/**
 * 实现token自动刷新有效期
 *
 * @author XQ
 * @date 2022/8/4
 */
public class AutoRefreshTokenStore extends RedisTokenStore {

    private ClientDetailsService clientDetailsService;

    public AutoRefreshTokenStore(RedisConnectionFactory connectionFactory, ClientDetailsService clientDetailsService) {
        super(connectionFactory);
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        OAuth2Authentication authentication = readAuthentication(token.getValue());
        if (authentication != null) {
            DefaultOAuth2AccessToken accessToken = (DefaultOAuth2AccessToken) token;
            int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
            if (validitySeconds > 0) {
                accessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
                this.storeAccessToken(accessToken, authentication);
            }
        }
        return authentication;
    }

    protected int getAccessTokenValiditySeconds(OAuth2Request clientAuth) {
        if (clientDetailsService != null) {
            ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getAccessTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }
        return 30 * 60;
    }
}
