package com.kee.common.security.annotation;


import com.kee.common.security.config.ResourceServerAutoConfiguration;
import com.kee.common.security.config.SecurityImportBeanDefinitionRegistrar;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import java.lang.annotation.*;

/**
 * @author zms
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.kee.**.mapper")
// 开启线程异步执行
@EnableAsync
// 开启资源服务配置
@EnableResourceServer

// 自动加载类
@Import({ ResourceServerAutoConfiguration.class, SecurityImportBeanDefinitionRegistrar.class })
public @interface EnableCustomConfig
{

}
