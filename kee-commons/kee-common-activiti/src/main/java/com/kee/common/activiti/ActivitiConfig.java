package com.kee.common.activiti;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description : Object
 * @author: zms
 */
@Configuration
public class ActivitiConfig {


    @Bean
    @Primary
    public ProcessEngineConfiguration processEngineConfiguration(PlatformTransactionManager platformTransactionManager
            , DataSource dataSource
            ){
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        DynamicRoutingDataSource routingDataSource = (DynamicRoutingDataSource) dataSource;
        Map<String, DataSource> dataSourceMap = routingDataSource.getDataSources();
        DataSource activiti = dataSourceMap.get("activiti");
        if(activiti==null){
            configuration.setDataSource(dataSource);
        }else {
            configuration.setDataSource(activiti);
        }
        configuration.setTransactionManager(platformTransactionManager);
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        return configuration;
    }
}
