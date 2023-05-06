package com.kee.common.reptiles.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description : Object
 * @author: zms
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "driver")
public class WebDriverAcquisitionConfig {
    private Acquisition acquisition;
    @Data
    public static class Acquisition{
        //本地驱动路径
        private String localPath;
        private String passWord;
        private String userName;
        private String button;
    }

}

