package com.kee.common.security.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
@ApiModel("消息")
public class Massage {

    private String id;

    private String tableName;

    private String type;

    private String name;
}
