package com.kee.gateway.handler;

import com.kee.common.core.exception.CaptchaException;
import com.kee.common.core.web.domain.AjaxResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Component
public class SmsCodeHandler implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        AjaxResult result = AjaxResult.success();
        return  ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(result));
    }
}
