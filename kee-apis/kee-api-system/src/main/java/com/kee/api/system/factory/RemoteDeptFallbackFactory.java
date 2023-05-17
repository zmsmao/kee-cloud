package com.kee.api.system.factory;

import com.kee.api.system.RemoteDeptService;
import com.kee.api.system.domain.SysDept;
import com.kee.common.core.domain.R;
import com.kee.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 部门远程服务降级处理
 * 
 * @author zms
 */
@Component
public class RemoteDeptFallbackFactory implements FallbackFactory<RemoteDeptService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteDeptFallbackFactory.class);

    @Override
    public RemoteDeptService create(Throwable throwable)
    {
        log.error("部门服务调用失败:{}", throwable.getMessage());
        return new RemoteDeptService() {

            @Override
            public AjaxResult list() {
                return AjaxResult.error("查询部门失败",throwable.getMessage());
            }

            @Override
            public AjaxResult tree() {
                return AjaxResult.error("查询部门树失败",throwable.getMessage());
            }

        };

    }
}
