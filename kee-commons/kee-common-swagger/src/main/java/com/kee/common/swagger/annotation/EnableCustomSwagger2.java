package com.kee.common.swagger.annotation;


import com.kee.common.swagger.config.SwaggerAutoConfiguration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * @author zms
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ SwaggerAutoConfiguration.class })
@EnableSwagger2
public @interface EnableCustomSwagger2
{

}
