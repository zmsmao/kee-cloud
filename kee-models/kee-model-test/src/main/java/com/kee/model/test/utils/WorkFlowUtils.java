package com.kee.model.test.utils;

import com.alibaba.fastjson.JSON;
import com.kee.api.system.RemoteUserService;
import com.kee.api.system.domain.SysUser;
import com.kee.common.core.text.UUID;
import com.kee.common.core.utils.DateUtils;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.utils.bean.BeanUtils;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.model.test.domain.TaskVo;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Component
public class WorkFlowUtils {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 当前任务直接跳跃到结束
     * @param taskId
     */
    @Transactional(rollbackFor = Exception.class)
    public void jumpEnd(String taskId,String approvalOpinions) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        FlowNode endFlowNode = endEventList.get(0);
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(execution.getActivityId());
        //  临时保存当前活动的原始方向
        List<SequenceFlow> originalSequenceFlowList = new ArrayList<>(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();
        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId(UUID.fastUUID().toString());
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        //添加意见
        if(StringUtils.isNotEmpty(approvalOpinions)){
            taskService.addComment(taskId, task.getProcessInstanceId(), "approvalOpinions", approvalOpinions);
        }
        //  完成当前任务
        taskService.complete(task.getId());
        //  恢复原型指定的方向
        currentFlowNode.setOutgoingFlows(originalSequenceFlowList);
    }

    /**
     * 跳跃至任意节点
     * @param taskId
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void jumpNode(String taskId,String id,String approvalOpinions) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        FlowNode sourceNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(execution.getActivityId());
        FlowNode targetNode = (FlowNode)  bpmnModel.getMainProcess().getFlowElement(id);
        //  临时保存当前活动的原始方向
        List<SequenceFlow> originalSequenceFlowList = new ArrayList<>(sourceNode.getOutgoingFlows());
        //创建新方向
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceFlowElement(sourceNode);
        flow.setTargetFlowElement(targetNode);
        flow.setId(UUID.fastUUID().toString());
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(flow);
        //清空所在的方向
        sourceNode.getOutgoingFlows().clear();
        //设置新方向
        sourceNode.setOutgoingFlows(newSequenceFlowList);
        //添加意见
        if(StringUtils.isNotEmpty(approvalOpinions)){
            taskService.addComment(taskId, task.getProcessInstanceId(), "approvalOpinions", approvalOpinions);
        }
        //  完成当前任务
        taskService.complete(task.getId());
        //  恢复原型指定的方向
        sourceNode.setOutgoingFlows(originalSequenceFlowList);
    }

    public List<FlowElement> flowElementList(String processId){
        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicActivityInstance.getProcessDefinitionId());
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        return new ArrayList<>(flowElements);
    }

    public List<TaskVo> approvalNodeTimeStatisticsAxis(String processInstanceId,String startTaskNodeName){
        List<TaskVo> taskVoList = getTaskInfoList(processInstanceId);
        return getTaskVoResultListByNode(taskVoList,startTaskNodeName);
    }

    private List<TaskVo> getTaskInfoList(String processInstanceId) {
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().asc().list();
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).activityType("userTask").
                orderByHistoricActivityInstanceStartTime().asc();
        List<HistoricActivityInstance> historicActivityInstanceList = historicActivityInstanceQuery.list();
        List<TaskVo> taskVoList = new ArrayList<>();
        historicActivityInstanceList.forEach(historicActivityInstance -> {
            TaskVo taskVo = getTaskVo(historicActivityInstance);
            historicTaskInstanceList.forEach(historicTaskInstance -> {
                if(StringUtils.equals(historicActivityInstance.getActivityName(),historicTaskInstance.getName())){
                    taskVo.setFormKey(historicTaskInstance.getFormKey());
                }
            });
            taskVoList.add(taskVo);
        });
        return taskVoList;
    }

    /**
     *  通过历史活动实例获取任务详情
     * @param historicActivityInstance 历史活动实例
     * @return TaskVo
     */
    private TaskVo getTaskVo(HistoricActivityInstance historicActivityInstance) {
        TaskVo taskVo = new TaskVo();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
        BeanUtils.copyProperties(historicActivityInstance, taskVo);
        if (StringUtils.isNotEmpty(historicActivityInstance.getActivityName())) {
            taskVo.setTaskName(historicActivityInstance.getActivityName());
        }
        if (StringUtils.isNotNull(historicActivityInstance.getStartTime())) {
            taskVo.setStartTime(dateFormat.format(historicActivityInstance.getStartTime()));
        }
        if (StringUtils.isNotNull(historicActivityInstance.getEndTime())) {
            taskVo.setEndTime(dateFormat.format(historicActivityInstance.getEndTime()));
        }
        taskVo.setUsername(getSysUser(taskVo.getAssignee()).getNickName());
        List<Comment> commentList = taskService.getTaskComments(historicActivityInstance.getTaskId());
        if (commentList.size() > 0) {
            String comment = commentList.get(0).getFullMessage();
            String[] messages = comment.split(",");
            if(messages.length>1){
                taskVo.setOperationName(messages[0]);
                taskVo.setComment(messages[1]);
            }else{
                taskVo.setComment(messages[0]);
            }

        }
        return taskVo;
    }

    private List<TaskVo> getTaskVoResultListByNode(List<TaskVo> taskVoList,String startTaskNodeName) {
        List<TaskVo> taskVoResultList = new ArrayList<>();
        taskVoList.forEach(taskVo -> {
            taskVoResultList.add(taskVo);
            if(taskVo.getTaskName().contains(startTaskNodeName)){
                taskVo.setTaskName("开始");
                taskVo.setOperationName("提交");
            }
        });
        TaskVo taskVo = taskVoResultList.get(taskVoResultList.size() - 1);
        if(StringUtils.isNotNull(taskVo.getEndTime())){
            TaskVo vo = new TaskVo();
            vo.setEndTime(taskVo.getEndTime());
            vo.setTaskName("结束");
            taskVoResultList.add(vo);
        }
        return taskVoResultList;
    }


    private SysUser getSysUser(String assignee){
        SysUser sysUser = new SysUser();
        if (StringUtils.isNotEmpty(assignee)) {
            AjaxResult ajaxResult = remoteUserService.getInfoById(Long.valueOf(assignee));
            if (ajaxResult.get("code").equals(200)) {
                sysUser= JSON.parseObject(JSON.toJSONString(ajaxResult.get("data")), SysUser.class);
            }
        }
        return sysUser;
    }

}
