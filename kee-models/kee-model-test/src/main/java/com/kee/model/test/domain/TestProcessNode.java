package com.kee.model.test.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.catalina.valves.JsonErrorReportValve;

import java.io.Serializable;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
public class TestProcessNode implements Serializable {

    private static final long serialVersionUID = 2L;

    @ApiModelProperty("审批意见")
    private String approvalOpinions;

    @ApiModelProperty("agree同意,return回退,disagree不同意")
    private String flag;

    @ApiModelProperty("业务id")
    private String id;

    @ApiModelProperty("流程实例")
    private String processId;

    @ApiModelProperty(value = "任务id",required = true)
    private String taskId;

    @ApiModelProperty(value = "自定义表单",required = true)
    private String formKey;

}
