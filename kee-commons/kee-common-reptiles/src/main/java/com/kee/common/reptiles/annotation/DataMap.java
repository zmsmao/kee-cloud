package com.kee.common.reptiles.annotation;

import java.lang.annotation.*;

/**
 * 表单填充
 * @Description : Object
 * @author: zms
 */
@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DataMap {


    String id() default "";

    String xpath() default "";

    DataType dataType() default DataType.INPUT_BOX;

    String format() default "yyyy-MM-dd";

    int waitTime() default 1000;
    /**
     * 有顺序下拉框使用
     * @return
     */
    int order() default Integer.MAX_VALUE;
    /**
     * 有序单属性单下拉框也可选择
     * 非下拉框选择时使用
     * @return
     */
    String[] xpathS() default {};


    enum  DataType{

        /**
         * 输入框,下拉框,日期框,ul框
         */
        INPUT_BOX(1),
        SELECTION_BOX(2),
        DATA_BOX(3),
        UL_BOX(4),
        FILE_BOX(5);

        private final int value;

        DataType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

}
