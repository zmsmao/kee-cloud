package com.kee.common.security.aspect;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.security.annotation.MassageTui;
import com.kee.common.security.domain.Massage;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Slf4j
@Aspect
@Component
public class AspectMassageTui {

    @Pointcut(value = "@annotation(com.kee.common.security.annotation.MassageTui)")
    public void point() {
    }

    @Around("point()")
    public Object aVoid(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed = pjp.proceed();
        Massage massage = new Massage();
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Object[] args = pjp.getArgs();
        //参数
        Class<?> parameter = null;
        int index = 0;
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                if(parameterAnnotations[i][j] instanceof RequestBody){
                    parameter =  method.getParameterTypes()[i];
                    index = i;
                    break;
                }
            }
        }

        MassageTui annotation = method.getDeclaredAnnotation(MassageTui.class);
        //类型
        massage.setType(annotation.type().getValue());
        //表名
        String tableName = annotation.tableName();
        massage.setTableName(tableName);
        //名称
        String name = annotation.name();
        massage.setName(name);
        //id
        String id = annotation.id();
        massage.setId(id);
        if (StringUtils.isEmpty(name)) {
            ApiModel apiModel = parameter.getDeclaredAnnotation(ApiModel.class);
            if(StringUtils.isNotNull(apiModel))
            {
                massage.setName(apiModel.value());
            }
        }

        if (StringUtils.isEmpty(tableName)) {
            TableName table = parameter.getDeclaredAnnotation(TableName.class);
            if(StringUtils.isNotNull(table))
            {
                massage.setTableName(table.value());
            }
        }

        if (StringUtils.isEmpty(id)) {
            Field[] declaredFields = parameter.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if(StringUtils.isNotNull(declaredField.getDeclaredAnnotation(TableId.class))){
                    Object o = declaredField.get(args[index]);
                    if(String.class == declaredField.getType() || o instanceof String){
                        if(StringUtils.isNotNull(o)){
                            massage.setId((String) o);
                        }
                    }
                    if(Long.class ==declaredField.getType() || o instanceof Long){
                        if(StringUtils.isNotNull(o)){
                            massage.setId(String.valueOf(o));
                        }
                    }
                    if(Integer.class ==declaredField.getType() || o instanceof Integer){
                        if(StringUtils.isNotNull(o)){
                            massage.setId(String.valueOf(o));
                        }
                    }
                    break;
                }
            }
        }
        System.out.println(massage);
        return proceed;
    }


}
