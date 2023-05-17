package com.kee.model.file.service;

import com.kee.api.system.domain.SysFile;
import com.kee.common.core.domain.R;
import io.minio.GetObjectResponse;
import io.minio.errors.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public interface ISysFileService {

    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    String upload(@RequestPart(value = "file") MultipartFile file) throws Exception;

    /**
     * 下载文件
     * @param url
     */
    void downloadFile(@RequestParam(value = "url") String url);

    /**
     * 删除文件
     * @param url
     * @throws Exception
     */
    void removeFile(@RequestParam(value = "url") String url) throws Exception;
}
