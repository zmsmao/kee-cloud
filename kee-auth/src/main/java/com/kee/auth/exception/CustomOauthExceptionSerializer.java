package com.kee.auth.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.kee.common.core.constant.HttpStatus;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 自定义异常返回
 *
 * @author zms
 **/
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException>
{
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(CustomOauthExceptionSerializer.class);

    public static final String BAD_CREDENTIALS = "Bad credentials";

    public CustomOauthExceptionSerializer()
    {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException
    {
        log.error(e.getMessage(), e.getMessage());
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField(AjaxResult.CODE_TAG, HttpStatus.ERROR);
        if (StringUtils.equals(e.getMessage(), BAD_CREDENTIALS))
        {
            jsonGenerator.writeStringField(AjaxResult.MSG_TAG, "用户名或密码错误");
        }
        else
        {
            log.warn("oauth2 认证异常 {} ", e);
            jsonGenerator.writeStringField(AjaxResult.MSG_TAG, e.getMessage());
        }
        jsonGenerator.writeEndObject();
    }
}