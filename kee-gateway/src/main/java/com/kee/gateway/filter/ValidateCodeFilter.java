package com.kee.gateway.filter;

import com.alibaba.fastjson.JSON;

import com.kee.common.core.constant.SecurityConstants;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.gateway.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证码过滤器
 *
 * @author zms
 */
@Component
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object> {
    private static final Pattern PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    private final static String AUTH_URL = "/oauth/token";

    @Autowired
    private ValidateCodeService validateCodeService;

    private static final String BASIC_ = "Basic ";

    private static final String CODE = "code";

    private static final String UUID = "uuid";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 非登录请求，不处理
            if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), AUTH_URL)) {
                return chain.filter(exchange);
            }

            MultiValueMap<String, String> queryParams;
            if (Objects.requireNonNull(request.getMethod()) == HttpMethod.POST) {
                String body = resolveBodyFromRequest(request);
                queryParams = getQueryParams(body);
            } else {
               queryParams = request.getQueryParams();
            }

            String grantType = queryParams.getFirst("grant_type");
            if (StringUtils.equals(SecurityConstants.CLIENT_CREDENTIALS, grantType)) {
                return chain.filter(exchange);
            }
            // 消息头存在内容，且不存在验证码参数，不处理
            String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isNotEmpty(header) && StringUtils.startsWith(header, BASIC_)
                    && !queryParams.containsKey(CODE) && !queryParams.containsKey(UUID)) {
                return chain.filter(exchange);
            }
            try {
                validateCodeService.checkCapcha(queryParams.getFirst(CODE),
                        queryParams.getFirst(UUID));
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                return exchange.getResponse().writeWith(
                        Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(AjaxResult.error(e.getMessage())))));
            }
            return chain.filter(exchange);
        };
    }

    private MultiValueMap<String, String> getQueryParams(String body) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        Matcher matcher = PARAM_PATTERN.matcher(body);
        while (matcher.find()) {
            String name = decodeQueryParam(matcher.group(1));
            String eq = matcher.group(2);
            String value = matcher.group(3);
            value = value != null ? decodeQueryParam(value) : (StringUtils.isNotEmpty(eq) ? "" : null);
            queryParams.add(name, value);
        }
        return queryParams;
    }

    private String decodeQueryParam(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            return URLDecoder.decode(value);
        }
    }

    private String resolveBodyFromRequest(ServerHttpRequest request) {
        // 获取请求体
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }
}
