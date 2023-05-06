package com.kee.model.system.controller;


import com.kee.api.system.domain.SysOpenLog;
import com.kee.common.core.utils.poi.ExcelUtil;
import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.core.web.page.TableDataInfo;
import com.kee.common.log.annotation.Log;
import com.kee.common.log.enums.BusinessType;
import com.kee.model.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 操作日志记录
 * 
 * @author trs
 */
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController
{
    @Autowired
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('system:operlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysOpenLog openLog)
    {
        startPage();
        List<SysOpenLog> list = operLogService.selectOperLogList(openLog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:export')")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOpenLog openLog) throws IOException
    {
        List<SysOpenLog> list = operLogService.selectOperLogList(openLog);
        ExcelUtil<SysOpenLog> util = new ExcelUtil<SysOpenLog>(SysOpenLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        operLogService.cleanOperLog();
        return AjaxResult.success();
    }

    @PostMapping
    public AjaxResult add(@RequestBody SysOpenLog openLog)
    {
        return toAjax(operLogService.insertOperlog(openLog));
    }
}
