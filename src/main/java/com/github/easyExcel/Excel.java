package com.github.easyExcel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {

    /**
     * 导入到Excel中的名字.
     */
    public String name();

    /**
     * 限制的长度.默认不限制
     */
    public int size() default 0;

    /**
     * 是否必填 默认不必填0  必填 1.
     */
    public int isNull() default 0;

    /**
     * 当值为空时,字段的默认值
     */
    public String defaultValue() default "";

    /**
     * 当值为空时,字段的默认值
     */
    public String dateFormat() default "";

    public int order() default 0;

}
