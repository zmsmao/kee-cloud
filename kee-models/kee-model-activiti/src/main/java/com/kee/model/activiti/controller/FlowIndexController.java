package com.kee.model.activiti.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Controller
public class FlowIndexController {

    /**
     * 流程编辑器建模页面路径
     */
    @GetMapping("/editor")
    public String editorPath(){
        return "modeler";
    }

}
