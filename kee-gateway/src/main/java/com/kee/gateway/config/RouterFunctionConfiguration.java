package com.kee.gateway.config;


import com.alibaba.fastjson.JSON;
import com.kee.gateway.handler.SmsCodeHandler;
import com.kee.gateway.handler.ValidateCodeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * 路由配置信息
 * 
 * @author zms
 */
@Configuration
public class RouterFunctionConfiguration
{
    @Autowired
    private ValidateCodeHandler validateCodeHandler;

    @Autowired
    private SmsCodeHandler smsCodeHandler;

    @SuppressWarnings("rawtypes")
    @Bean
    public RouterFunction routerFunction()
    {
        return RouterFunctions
                .route(RequestPredicates.GET("/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                validateCodeHandler)
                .andOther(
                        RouterFunctions.route(RequestPredicates.POST("/sms/login").and(RequestPredicates.accept(MediaType.valueOf(MediaType.TEXT_HTML_VALUE)))
                        ,smsCodeHandler));

    }

}
