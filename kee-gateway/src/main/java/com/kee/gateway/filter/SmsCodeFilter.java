package com.kee.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kee.common.core.domain.SmsCode;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.ServletWebRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Component
public class SmsCodeFilter extends AbstractGatewayFilterFactory<Object>{

    private static final String PATH_URL = "/sms/login";

    private final static String SMS_PHONE="sms:phone:";

    @Override
    public String name() {
        return "SmsCodeFilter";
    }

    private static final Pattern PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    @Override
    public GatewayFilter apply(Object config) {
       return (exchange, chain) -> {

           ServerHttpRequest request = exchange.getRequest();
           // 非登录请求，不处理
           if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), PATH_URL)) {
               return chain.filter(exchange);
           }
           try {
//               Map<String, Object> body = parseJsonRequestBody(request);
               MultiValueMap<String, String> queryParams;
               if (Objects.requireNonNull(request.getMethod()) == HttpMethod.POST) {
                   String body = ValidateCodeFilter.resolveBodyFromRequest(request);
                   queryParams = ValidateCodeFilter.getQueryParams(body);
               } else {
                   queryParams = request.getQueryParams();
               }
               validateSmsCode(queryParams);
           } catch (Exception e){
               ServerHttpResponse response = exchange.getResponse();
               return exchange.getResponse().writeWith(
                       Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(AjaxResult.error(e.getMessage())))));

           }
           return chain.filter(exchange);
       };
    }

    private static Map<String, Object> parseJsonRequestBody(ServerHttpRequest request) {
        Flux<DataBuffer> body = request.getBody();
        Map<String,Object> map = new HashMap<>();
        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            try {
                String bodyString = new String(bytes, "utf-8");
                System.out.println(bodyString);
                JSONObject jsonObject = JSONObject.parseObject(bodyString);
                Map<String, Object> innerMap = jsonObject.getInnerMap();
                map.putAll(innerMap);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
       return map;
    }

    private void validateSmsCode(MultiValueMap<String,String> map) throws Exception {
        Object smsCode = map.get("code");
        Object phone = map.get("phone");
        if(StringUtils.isNull(phone)){
            throw new  RuntimeException("请求体无phone参数");
        }
        if (StringUtils.isNull(smsCode))
        {
            throw new  RuntimeException("请求体无code参数");
        }
        String phoneStr = ((List<String>)phone).get(0);
        if (StringUtils.isEmpty(phoneStr)) {
            throw new RuntimeException("手机号码不能为空！");
        }
        String smsCodeStr = ((List<String>)smsCode).get(0);
        if (StringUtils.isEmpty(smsCodeStr)) {
            throw new RuntimeException("验证码不能为空！");
        }
//        SmsCode cacheObject = redisService.getCacheObject(SMS_PHONE + phoneStr);
//        System.out.println(SMS_PHONE+phoneStr);
//        if(StringUtils.isNull(cacheObject))
//        {
//            redisService.deleteObject(SMS_PHONE + phoneStr);
//            throw new RuntimeException("验证码不存在，请重新发送！");
//        }
//        if(cacheObject.isExpire())
//        {
//            redisService.deleteObject(SMS_PHONE + phoneStr);
//            throw new RuntimeException("验证码已过期，请重新发送！");
//        }
//        if (!cacheObject.getCode().equals(smsCodeStr)) {
//            redisService.deleteObject(SMS_PHONE + phoneStr);
//            throw new RuntimeException("验证码不正确，请重新发送！");
//        }
//        if (!cacheObject.getPhone().equals(phoneStr)) {
//            redisService.deleteObject(SMS_PHONE + phoneStr);
//            throw new RuntimeException("手机号码不正确，请重新发送！");
//        }
    }

}
