package com.kee.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.web.client.RestTemplate;

/**
 * oauth2 服务配置
 * 
 * @author zms
 */
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
    @Autowired
    protected RemoteTokenServices remoteTokenServices;

//    @Bean
//    public RemoteTokenServices remoteTokenServices() {
//        return remoteTokenServices = new RemoteTokenServices();
//    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthIgnoreConfig authIgnoreConfig;

    @Override
    public void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        // 不登录可以访问
        authIgnoreConfig.getUrls().forEach(url -> registry.antMatchers(url).permitAll());
        registry.anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
    {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        UserAuthenticationConverter userTokenConverter = new CommonUserConverter();
        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        remoteTokenServices.setRestTemplate(restTemplate);
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);
        resources.tokenServices(remoteTokenServices);
    }
}
