package com.kee.model.file.service.impl;

import com.kee.api.system.domain.SysFile;
import com.kee.common.core.domain.R;
import com.kee.common.core.exception.CustomException;
import com.kee.model.file.config.MinioServerConfig;
import com.kee.model.file.service.ISysFileService;
import com.kee.model.file.utils.FileUploadUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Service
public class SysFileServiceImpl implements ISysFileService {

    @Resource
    MinioClient minioClient;

    @Resource
    MinioServerConfig serverConfig;

    @Resource
    private HttpServletResponse httpServletResponse;


    @Override
    public String upload(MultipartFile file) throws Exception {

        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        for (String s : serverConfig.getNoUploadFile()) {
            if(fileSuffix.equals(s)) {
                throw new CustomException("禁止上传" + s + "文件！", 500);
            }
        }
        
        String filename = FileUploadUtils.realFilename(file);
        existBucket(serverConfig.getBucketName().get(0));
        minioClient.putObject(buildPutObject(filename, file));
        return "/" + serverConfig.getBucketName().get(0) + "/" + filename;
    }

    @Override
    public void downloadFile(String url) {
        String[] split = url.split(serverConfig.getBucketName().get(0));
        GetObjectArgs args = GetObjectArgs.builder().bucket(serverConfig.getBucketName().get(0)).object(split[1].substring(1)).build();
        try (GetObjectResponse response = minioClient.getObject(args)){
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
                while ((len=response.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                httpServletResponse.setCharacterEncoding("utf-8");
                //设置强制下载不打开
                httpServletResponse.setContentType("application/force-download");
                httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + url);
                try (ServletOutputStream stream = httpServletResponse.getOutputStream()){
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeFile(String url) throws Exception {
        String[] split = url.split(serverConfig.getBucketName().get(0));
        System.out.println(split[1].substring(1));
        minioClient.deleteObjectTags(
                DeleteObjectTagsArgs.builder()
                        .bucket(serverConfig.getBucketName().get(0))
                        .object(split[1].substring(1))
                        .build()
        );
    }

    /**
     * description: 判断bucket是否存在，不存在则创建
     *
     * @return: void
     */
    private void existBucket(String name) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PutObjectArgs buildPutObject(String filename, MultipartFile file) throws IOException {
        return PutObjectArgs.builder()
                .bucket(serverConfig.getBucketName().get(0))
                .object(filename)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();

    }

}
