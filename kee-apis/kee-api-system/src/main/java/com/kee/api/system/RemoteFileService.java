package com.kee.api.system;


import com.kee.api.system.domain.SysFile;
import com.kee.api.system.factory.RemoteFileFallbackFactory;
import com.kee.common.core.constant.ServiceNameConstants;
import com.kee.common.core.domain.R;
import io.minio.GetObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 * 
 * @author zms
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService
{
    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<SysFile> upload(@RequestPart(value = "file") MultipartFile file);

    @GetMapping(value = "/file/download")
    GetObjectResponse downloadFile(@RequestParam(value = "url") String url);

    @DeleteMapping(value = "/file/remove")
    void removeFile(@RequestParam(value = "url") String url);
}
