package com.kee.model.file.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Configuration
public class MinioCilentConfig {

    @Resource
    private MinioServerConfig serverConfig;

    @Bean
    public MinioClient minioClient(){
      return MinioClient.builder().endpoint(serverConfig.getEndpoint())
               .credentials(serverConfig.getAccessKey(),serverConfig.getSecretKey()).build();
    }
}
