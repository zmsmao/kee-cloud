package com.kee.model.test.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.activiti.engine.history.HistoricActivityInstance;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
public class TaskVo {
    private String taskId;
    @ApiModelProperty("处理环节")
    private String taskName;
    private String processInstanceId;
    private String executionId;
    private String businessKey;
    private String processDefinitionName;
    private String starter;
    private String assignee;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private String startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("签收时间/结束时间")
    private String endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    private String formKey;
    @ApiModelProperty("处理意见")
    private String comment;
    @ApiModelProperty("处理结果")
    private String operationName;
    @ApiModelProperty("处理人")
    private String username;
}
