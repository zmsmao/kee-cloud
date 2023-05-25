package com.kee.model.test.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.security.utils.SecurityUtils;
import com.kee.model.test.domain.Test;
import com.kee.model.test.domain.TestOptions;
import com.kee.model.test.domain.TestProcessNode;
import com.kee.model.test.mapper.TestMapper;
import com.kee.model.test.service.ITestService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {


    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;


    private final static String LEAVE_PROCESS = "LeaveProcess";


    @Override
    public void startProcess(Test test) {
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", "1");
        map.put("teacherId", test.getReviewUser());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(LEAVE_PROCESS, String.valueOf(test.getTestId()), map);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        taskService.complete(task.getId());
        test.setCreator("admin");
        test.setCreateTime(new Date());
        test.setCheckStatus("0");
        test.setStatus("1");
        test.setProcessId(processInstance.getProcessInstanceId());
        saveOrUpdate(test);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrEdit(Test test) {
        if (StringUtils.isNull(test.getTestId())) {
            if ("0".equals(test.getSubmit())) {
                save(test);
            } else {
                saveOrUpdate(test);
                startProcess(test);
                return;
            }
        }
        Test byId = getById(test.getTestId());
        if ("0".equals(byId.getSubmit()) && "1".equals(test.getSubmit())) {
            startProcess(test);
        }
        if("3".equals(byId.getStatus())){
            Map<String, Object> map = new HashMap<>();
            map.put("teacherId", test.getReviewUser());
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(test.getProcessId()).singleResult();
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list().get(0);
            test.setEditor("admin");
            test.setEditTime(new Date());
            test.setCheckStatus("0");
            test.setStatus("1");
            taskService.complete(task.getId());
        }
        saveOrUpdate(test);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyNode(TestProcessNode processNode) {
        Test test = getById(processNode.getId());
        Map<String, Object> map = new HashMap<>(16);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processNode.getProcessId()).singleResult();
        taskService.addComment(test.getTestTaskId(), processInstance.getProcessInstanceId(), "approvalOpinions", processNode.getApprovalOpinions());
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).taskId(processNode.getTaskId()).singleResult();
        if (TestOptions.AGREE.equals(processNode.getFlag())) {
            map.put("flag", processNode.getFlag());
            test.setStatus("4");
            test.setCheckStatus("1");
        }
        if (TestOptions.DISAGREE.equals(processNode.getFlag())) {
            map.put("flag", processNode.getFlag());
            test.setStatus("2");
            test.setCheckStatus("2");
        }
        if (TestOptions.RETURN.equals(processNode.getFlag())) {
            map.put("flag", processNode.getFlag());
            test.setStatus("3");
            test.setCheckStatus("3");
        }
        updateById(test);
        taskService.complete(task.getId(), map);
    }

    @Override
    public List<FlowElement> allNodeList() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(LEAVE_PROCESS).latestVersion().singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        return new ArrayList<>(flowElements);
    }

    @Override
    public List<Test> futureCheck() {
        String assignee = "1";
        List<Test> tests = list(Wrappers.lambdaQuery(Test.class).isNotNull(Test::getProcessId))
                .stream().filter(one -> StringUtils.isNotEmpty(one.getProcessId()) &&!"3".equals(one.getStatus())).collect(Collectors.toList());
        for (Test test : tests) {
            List<Task> task = taskService.createTaskQuery().processInstanceId(test.getProcessId()).taskAssignee(assignee).list();
            if(!task.isEmpty()) {
                test.setTestTaskId(task.get(0).getId());
            }
        }
        System.out.println("ok");
        return tests;
    }

    @Override
    public List<Test> completeCheck(){
        String assignee = "1";
        List<Test> tests = list(Wrappers.lambdaQuery(Test.class).isNotNull(Test::getProcessId))
                .stream().filter(one -> StringUtils.isNotEmpty(one.getProcessId())).collect(Collectors.toList());
        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .processDefinitionKey(LEAVE_PROCESS)
                .finished()
                .list();
        Set<String> processInstanceIds = new HashSet<>();
        for (HistoricTaskInstance str : taskList) {
            String formKey = str.getFormKey().split("/")[1];
            String processId = str.getProcessInstanceId();
            if (!processInstanceIds.contains(processId) && !"reEdit".equals(formKey)) {
                processInstanceIds.add(processId);
            }
        }
        Set<String> businessKeys = new HashSet<>();
        if (!processInstanceIds.isEmpty()) {
            // 根据历史流程实例，获得业务key
            List<HistoricProcessInstance> hisProcessList = historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).orderByProcessInstanceId().desc().list();
            for (HistoricProcessInstance p : hisProcessList) {
                String businessKey = p.getBusinessKey();
                businessKeys.add(businessKey);
            }
        }
        return list(Wrappers.lambdaQuery(Test.class).in(Test::getTestId, businessKeys));
    }

}
