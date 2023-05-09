package com.kee.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
public class SmsCode implements Serializable{

    private static final long serialVersionUID = 1L;

    private String code;

    private String phone;

    private LocalDateTime dateTime;

    private String effective;

   public boolean isExpire() {
        return LocalDateTime.now().isAfter(dateTime);
    }
}
