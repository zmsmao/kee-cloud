package com.kee.model.activiti.controller;

import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Controller
@Api(tags = "流程编辑器建模页面")
public class FlowIndexController extends BaseController {

    /**
     * 流程编辑器建模页面路径
     */
    @ApiOperation(value = "流程编辑器建模页面路径")
    @GetMapping("/editor")
    public String editorPath(){
        return "modeler";
    }

}
