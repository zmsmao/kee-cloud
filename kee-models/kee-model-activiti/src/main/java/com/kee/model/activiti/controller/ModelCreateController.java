package com.kee.model.activiti.controller;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.model.activiti.domain.ModelParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Controller
@RequestMapping("/activiti/model")
public class ModelCreateController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;


    @ApiOperation("保存模型")
    @PostMapping("/save")
    public AjaxResult saveModel(@RequestBody ModelParam modelParamDto) throws JsonProcessingException {
        Model model = repositoryService.newModel();
        model.setName(modelParamDto.getName());
        model.setKey(modelParamDto.getKey());
        model.setCategory(modelParamDto.getClassification());
        model.setVersion(modelParamDto.getVersion());
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME,modelParamDto.getName());
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,modelParamDto.getDescription());
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION,modelParamDto.getVersion());
        model.setMetaInfo(modelNode.toString());

        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(modelParamDto.getKey()).list();
        if(CollectionUtils.isNotEmpty(list)){
            return AjaxResult.error("模型标识已存在，请重新填写！");
        }else{
            repositoryService.saveModel(model);
            Map<String, Object> contentMap = new HashMap<>();
            Map<String, Object> propertiesMap = new HashMap<>();
            Map<String, Object> stenclisetMap = new HashMap<>();
            propertiesMap.put("process_id",modelParamDto.getKey());
            propertiesMap.put("name",modelParamDto.getName());
            propertiesMap.put("category",modelParamDto.getClassification());
            stenclisetMap.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
            contentMap.put("resourceId",model.getId());
            contentMap.put("properties",propertiesMap);
            contentMap.put("stencilset",stenclisetMap);
            repositoryService.addModelEditorSource(model.getId(),objectMapper.writeValueAsBytes(contentMap));
            return AjaxResult.success(model);
        }
    }
}
