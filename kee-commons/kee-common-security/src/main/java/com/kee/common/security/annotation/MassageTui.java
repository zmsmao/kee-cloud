package com.kee.common.security.annotation;

import com.kee.common.core.annotation.Excel;

import java.lang.annotation.*;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MassageTui {

    String tableName() default "";
    TypeE type() default TypeE.ADD;
    String id() default "";
    String name() default "";

    enum  TypeE{
         /**
          * 修改，新增
          */
         EDIT("1"),ADD("2");
         private final String value;
        private TypeE(String value){
            this.value = value;
        }
         public String getValue() {
             return value;
         }
     }
}
