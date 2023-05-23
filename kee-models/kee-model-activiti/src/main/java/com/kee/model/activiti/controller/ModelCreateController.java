package com.kee.model.activiti.controller;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.model.activiti.domain.ModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@RestController
@RequestMapping("/activiti/model")
@Api(tags = "模型")
public class ModelCreateController extends BaseController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/list")
    @ApiOperation(value = "查询所有模型")
    public AjaxResult getModels() {
        return AjaxResult.success("查询成功",
                repositoryService.createModelQuery()
                .latestVersion().orderByCreateTime().desc().list());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除模型")
    public AjaxResult deleteModel(@PathVariable("id") String id){
        repositoryService.deleteModel(id);
        return AjaxResult.success();
    }

    @GetMapping(value = "/export/{modelId}", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation(value = "导出模型")
    public void exportModelAsXml(@PathVariable String modelId, HttpServletResponse response) throws IOException {
        byte[] modelEditorSource = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = objectMapper.readTree(modelEditorSource);
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
        byte[] bytes = new BpmnXMLConverter().convertToXML(bpmnModel, "UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        IOUtils.copy(inputStream, response.getOutputStream());
        String fileName = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Type", "application/xml");
        response.flushBuffer();
    }

    @ApiOperation("发布流程模型")
    @GetMapping("/deploy/{modelId}")
    @Transactional
    public AjaxResult flowModelDeploy(@PathVariable String modelId) {
        try {
            Model model = repositoryService.getModel(modelId);
            byte[] modelEditorSource = repositoryService.getModelEditorSource(modelId);
            JsonNode jsonNode = objectMapper.readTree(modelEditorSource);
            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
            Deployment deploy = repositoryService.createDeployment().category(model.getCategory()).name(model.getName())
                    .key(model.getKey()).addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel).deploy();
            model.setDeploymentId(deploy.getId());
            repositoryService.saveModel(model);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("流程图不合规范，请重新设计 !");
        }
        return AjaxResult.success();
    }


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
