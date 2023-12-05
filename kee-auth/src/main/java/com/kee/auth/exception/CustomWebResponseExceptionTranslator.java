package com.kee.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * OAuth2 自定义异常处理
 * 
 * @author zms
 */
@Slf4j
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception>
{
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e)
    {
        log.error(e.getMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(new CustomOauthException(e.getMessage()));
    }
}
