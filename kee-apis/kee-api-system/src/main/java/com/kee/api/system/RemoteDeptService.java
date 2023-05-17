package com.kee.api.system;
import com.kee.api.system.factory.RemoteDeptFallbackFactory;
import com.kee.common.core.constant.ServiceNameConstants;
import com.kee.common.core.web.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 部门远程服务
 *
 * @author zms
 * @date 2021/10/22
 */
@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService {

    @GetMapping("/dept/list")
    AjaxResult list();

    @GetMapping("/dept/treeselect")
    AjaxResult tree();
}
