package com.kee.api.system.factory;

import com.kee.api.system.RemoteJobService;
import com.kee.api.system.domain.SysJob;
import com.kee.common.core.domain.R;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.core.web.page.TableDataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Component
public class RemoteJobFallbackFactory implements FallbackFactory<RemoteJobService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteJobFallbackFactory.class);

    @Override
    public RemoteJobService create(Throwable cause) {
        log.error("定时服务调用失败:{}", cause.getMessage());
        return new RemoteJobService() {
            @Override
            public R<SysJob> selectByJobName(String jobName) {
                return  R.fail();
            }

            @Override
            public R<Integer> deleteJobByJobName(String jobName) {
                return R.fail();
            }

            @Override
            public R<TableDataInfo> list(SysJob sysJob) {
                return R.fail();
            }

            @Override
            public AjaxResult add(SysJob sysJob) {
                return AjaxResult.error();
            }

            @Override
            public AjaxResult changeStatus(SysJob job) {
                return AjaxResult.error();
            }

            @Override
            public AjaxResult remove(Long[] jobIds) {
                return AjaxResult.error();
            }
        };
    }
}
