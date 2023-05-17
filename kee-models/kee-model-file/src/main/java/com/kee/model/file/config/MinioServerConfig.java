package com.kee.model.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioServerConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private List<String> bucketName;
    private List<String> noUploadFile;
}
