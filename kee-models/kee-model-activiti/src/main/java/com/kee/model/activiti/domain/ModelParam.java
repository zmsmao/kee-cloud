package com.kee.model.activiti.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
@ApiModel(value = "模型参数Dto",description = "模型参数Dto")
public class ModelParam {

    @ApiModelProperty("模型名")
    private String name;
    @ApiModelProperty("模型标识")
    private String key;
    @ApiModelProperty("分类")
    private String classification;
    @ApiModelProperty("版本号")
    private Integer version;
    @ApiModelProperty("模型描述")
    private String  description;
}
