package com.kee.model.file.controller;

import com.kee.api.system.domain.SysFile;
import com.kee.common.core.domain.R;
import com.kee.common.core.utils.file.FileUtils;
import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.model.file.service.ISysFileService;
import io.minio.GetObjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@RestController
@RequestMapping
public class SysFileController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);

    @Resource
    private ISysFileService sysFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<SysFile> upload(@RequestPart(value = "file") MultipartFile file){
        try
        {
            // 上传并返回访问地址
            String url = sysFileService.upload(file);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url));
            sysFile.setUrl(url);
            return R.ok(sysFile);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping(value = "/download")
    public void downloadFile(@RequestParam(value = "url") String url){
         sysFileService.downloadFile(url);
    }

    @DeleteMapping(value = "/remove")
    public AjaxResult removeFile(@RequestParam(value = "url") String url){
        try {
            sysFileService.removeFile(url);
            return AjaxResult.success("删除成功");
        }catch (Exception e){
            log.error("删除文件失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }
}
