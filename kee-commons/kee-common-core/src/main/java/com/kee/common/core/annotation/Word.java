package com.kee.common.core.annotation;

import java.lang.annotation.*;

/**
 * @Description : TODO
 * @author zms
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Word {

    String name() default "";

    TypeField type() default TypeField.NATURE;

    DateFormatType format() default DateFormatType.DATENOTDETAIL;

    /**
     * 确定导出类型
     */
    enum TypeField {
        /**
         * 普通,list,富文本,附件(仅仅支出xlsx,docx)
         */
        NATURE(0), LIST(1), HTMLTEXT(2),ATTACHMENT(3);
        private final int value;

        TypeField(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum DateFormatType {
        /**
         * 格式化日期
         */
        DATENOTDETAIL("yyyy-MM-dd"), DATEDETAIL("yyyy-MM-dd HH:ss:mm");
        private final String value;

        DateFormatType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
