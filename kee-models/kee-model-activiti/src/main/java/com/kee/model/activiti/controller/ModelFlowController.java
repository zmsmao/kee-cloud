package com.kee.model.activiti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kee.common.core.constant.HttpStatus;
import com.kee.common.core.utils.DateUtils;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.utils.bean.BeanUtils;
import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.core.web.page.TableDataInfo;
import com.kee.model.activiti.domain.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@RestController
@RequestMapping("/activiti/flow")
@Api(tags = "已部署的模型的流程定义")
public class ModelFlowController extends BaseController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;


    @ApiOperation("查询已部署的工作流列表")
    @GetMapping("/list")
    public TableDataInfo getProcessList(@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "pageNum") Integer pageNum, String name, String key, @RequestParam(value = "sort") Boolean sort) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotEmpty(name)) {
            processDefinitionQuery.processDefinitionNameLike(name);
        }
        if (StringUtils.isNotEmpty(key)) {
            processDefinitionQuery.processDefinitionKeyLike(key);
        }
        if (sort) {
            processDefinitionQuery.latestVersion();
        }
        int startIndex = (pageNum - 1) * pageSize;
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.orderByDeploymentId().desc().listPage(startIndex, pageSize);
        List<ProcessVo> processVoList = new ArrayList<>();
        processDefinitionList.forEach(processDefinition -> {
            ProcessVo processVo = new ProcessVo();
           BeanUtils.copyBeanProp(processVo, processDefinition);
            processVo.setIsSuspended(processDefinition.isSuspended());
            processVo.setDiagramresourceName(processDefinition.getDiagramResourceName());
            processVoList.add(processVo);

        });
        TableDataInfo tableDataInfo = new TableDataInfo(HttpStatus.SUCCESS,"查询成功！");
        tableDataInfo.setTotal(processDefinitionQuery.list().size());
        tableDataInfo.setRows(processVoList);
        return tableDataInfo;
    }

    /**
     * 上传并部署工作流文件
     * @param file
     * @return
     */
    @PostMapping("/upload/flow")
    @ApiOperation("上传并部署工作流文件")
    public AjaxResult uploadFlowFile(@RequestParam MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            if (filename.endsWith("bpmn") || filename.endsWith("xml")) {
                repositoryService.createDeployment().name(filename).addInputStream(filename, inputStream).deploy();
            } else {
                return AjaxResult.error("文件格式错误！！！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("部署失败！！！");
        }
        return AjaxResult.success("部署成功！");
    }

    /**
     * 流程定义转化为模型
     * @param processDefinitionId
     * @return
     */
    @ApiOperation("流程定义转化为模型")
    @GetMapping("/exchange/process/{processDefinitionId}")
    public AjaxResult exchangeProcessToModel(@PathVariable String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Model model = repositoryService.newModel();
        model.setName(processDefinition.getName());
        model.setCategory(processDefinition.getCategory());
        model.setKey(processDefinition.getKey());
        model.setDeploymentId(processDefinition.getDeploymentId());
        ObjectNode modelData = objectMapper.createObjectNode();
        modelData.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelData.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        List<Model> modelList = repositoryService.createModelQuery().modelKey(processDefinition.getKey()).list();
        if (modelList.size() > 0) {
            Integer version = modelList.get(0).getVersion();
            version++;
            modelData.put(ModelDataJsonConstants.MODEL_REVISION, version);
            repositoryService.deleteModel(modelList.get(0).getId());
            model.setVersion(version);
        } else {
            modelData.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        }
        model.setMetaInfo(modelData.toString());
        repositoryService.saveModel(model);
        ObjectNode jsonNodes = new BpmnJsonConverter().convertToJson(bpmnModel);
        repositoryService.addModelEditorSource(model.getId(), jsonNodes.toString().getBytes(StandardCharsets.UTF_8));
        return AjaxResult.success("转化成功！", jsonNodes.toString());
    }

    /**
     * 删除一个已部署的工作流
     * @param deploymentId
     * @return
     */
    @ApiOperation("删除一个已部署的工作流")
    @DeleteMapping("/remove/{deploymentId}")
    public AjaxResult deleteDeployedWorkflows(@PathVariable String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return AjaxResult.success();
    }

    /**
     * 挂起一个已部署的工作流
     * @param processDefinitionId
     * @param flag
     * @param date
     * @return
     */
    @ApiOperation("挂起一个已部署的工作流")
    @PutMapping("/suspend/{processDefinitionId}")
    public AjaxResult suspendDeployedWorkflows(@PathVariable String processDefinitionId, Boolean flag,String date) {

        if (StringUtils.isNotEmpty(date) && flag) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
            try {
                repositoryService.suspendProcessDefinitionById(processDefinitionId, flag, dateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            flag = true;
            repositoryService.suspendProcessDefinitionById(processDefinitionId, flag, null);
        }
        return AjaxResult.success();
    }

    /**
     * 激活一个流程定义
     * @param processDefinitionId
     * @param flag
     * @param date
     * @return
     * @throws ParseException
     */
    @ApiOperation("激活一个流程定义")
    @PutMapping("/activate/{processDefinitionId}")
    public AjaxResult activationOnProcessDefinition(@PathVariable String processDefinitionId, @RequestParam("flag") Boolean
            flag, @RequestParam("date") String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
        if(StringUtils.isNotEmpty(date)){
            repositoryService.activateProcessDefinitionById(processDefinitionId,flag,dateFormat.parse(date));
        }else{
            repositoryService.activateProcessDefinitionById(processDefinitionId,flag,null);
        }
        return AjaxResult.success();
    }
}
