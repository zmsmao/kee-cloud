package com.kee.model.test.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description : Object
 * @author zms
 */
@Data
@TableName("test")
public class Test implements Serializable {

    static final long serialVersionUID = 49243434L;

    @TableId(type = IdType.AUTO)
    private Long testId;

    private String testName;

    private String testTask;

    @TableField(exist = false)
    private String testTaskId;

    private String processId;

    @ApiModelProperty("当前所在节点，即审核，0待提交,1老师待审,2不通过，3退回修改，4审核通过")
    private String status;

    @ApiModelProperty("当前节点审核的状态,0待审，1通过，2不通过，3退回修改")
    private String checkStatus;

    @ApiModelProperty("是否开启流程,0不开启,1开启")
    private String submit;

    @TableField(exist = false)
    @ApiModelProperty("审批人")
    private String reviewUser;


    @ApiModelProperty("节点跳跃，直接结束")
    private String jumpStatus;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT, select = false)
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 最后修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    @ApiModelProperty(value = "最后修改人")
    private String editor;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    @ApiModelProperty(value = "最后修改时间")
    private Date editTime;
}
