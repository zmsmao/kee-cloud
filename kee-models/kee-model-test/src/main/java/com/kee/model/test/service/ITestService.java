package com.kee.model.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kee.model.test.domain.Test;
import com.kee.model.test.domain.TestProcessNode;
import org.activiti.bpmn.model.FlowElement;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public interface ITestService extends IService<Test> {

    /**
     * 开启流程
     * @param test
     */
    void startProcess(Test test);

    /**
     * 更新或者保存
     * @param test
     */
    void saveOrEdit(Test test);

    /**
     * 待审列表
     * @return
     */
    List<Test> futureCheck();

    /**
     * 已审批列表
     * @return
     */
    List<Test> completeCheck();


    /**
     * 审批节点
     * @param processNode
     */
    void applyNode(TestProcessNode processNode);

    /**
     * 获取所有的节点
     * @return
     */
    List<FlowElement> allNodeList();

}
