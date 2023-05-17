package com.kee.api.system;

import com.kee.api.system.domain.SysJob;
import com.kee.api.system.factory.RemoteJobFallbackFactory;
import com.kee.common.core.constant.ServiceNameConstants;
import com.kee.common.core.domain.R;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.core.web.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "remoteJobService",value = ServiceNameConstants.JOB_SERVICE,fallbackFactory = RemoteJobFallbackFactory.class)
public interface RemoteJobService {

    /**
     * 根据job名称查询
     * @param jobName
     * @return
     */
    @GetMapping("/job/selectByJobName")
    R<SysJob> selectByJobName(@RequestParam("jobName") String jobName);

    /**
     * 根据job名称删除
     * @param jobName
     * @return
     */
    @DeleteMapping("/job/deleteJobByJobName")
    R<Integer> deleteJobByJobName(@RequestParam("jobName")String jobName);

    /**
     * 查询定时任务列表
     * @param sysJob
     * @return
     */
    @GetMapping("/job/list")
    R<TableDataInfo> list(@RequestParam("sysJob") SysJob sysJob);

    /**
     * 新增定时任务
     * @param sysJob
     * @return
     */
    @PostMapping("/job")
    AjaxResult add(@RequestBody SysJob sysJob);

    /**
     * 修改定时任务
     * @param job
     * @return
     */
    @PutMapping("/job/changeStatus")
    AjaxResult changeStatus(@RequestBody SysJob job);

    /**
     * 删除定时任务
     * @param jobIds
     * @return
     */
    @DeleteMapping("/{jobIds}")
    AjaxResult remove(@PathVariable("jobIds") Long[] jobIds);
}
