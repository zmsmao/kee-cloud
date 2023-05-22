package com.kee.model.activiti.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
@ApiModel(value = "流程信息Vo")
public class ProcessVo {
    @ApiModelProperty("流程定义编号")
    private String id;
    @ApiModelProperty("流程部署编号")
    private String deploymentId;
    @ApiModelProperty("流程名称")
    private String name;
    @ApiModelProperty("流程定义键")
    private String key;
    @ApiModelProperty("流程资源定义名称")
    private String resourceName;
    private String diagramresourceName;
    @ApiModelProperty("版本号")
    private Integer version;
    @ApiModelProperty("状态，状态为false则是启动，状态为true则是挂起")
    private Boolean isSuspended;

}