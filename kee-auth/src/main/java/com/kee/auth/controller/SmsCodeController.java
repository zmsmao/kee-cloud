package com.kee.auth.controller;

import com.kee.common.core.domain.SmsCode;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.redis.service.RedisService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@RestController
@RequestMapping("/sms")
public class SmsCodeController extends BaseController {


    @Resource
    private RedisService redisService;

    private final static String SMS_PHONE="sms:phone:";

    @PostMapping("/send")
    public AjaxResult send(@RequestBody SmsCode code){
        int time = 300;
        if(StringUtils.isNotNull(redisService.getCacheObject(SMS_PHONE+code.getPhone())))
        {
            return AjaxResult.error("该验证码已发送，请"+time+"s之后再试");
        }
        String smsCode = RandomStringUtils.randomNumeric(6);
        code.setCode(smsCode);
        code.setDateTime(LocalDateTime.now().plusSeconds(time));
        code.setEffective("true");
        redisService.setCacheObject(SMS_PHONE+code.getPhone(),code, (long) time, TimeUnit.SECONDS);
        System.out.println(SMS_PHONE+code.getPhone());
        return AjaxResult.success("有效时间为"+time+"s",code);
    }
}
