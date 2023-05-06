package com.kee.model.test.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

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
}
