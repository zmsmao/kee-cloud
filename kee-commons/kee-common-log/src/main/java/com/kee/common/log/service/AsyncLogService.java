package com.kee.common.log.service;

import com.kee.api.system.RemoteLogService;
import com.kee.api.system.domain.SysOpenLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步调用日志服务
 *
 * @author zms
 */
@Service
public class AsyncLogService {

    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOpenLog sysOpenLog) {
        remoteLogService.saveLog(sysOpenLog);
    }
}
