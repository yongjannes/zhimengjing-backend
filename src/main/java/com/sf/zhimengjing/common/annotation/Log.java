package com.sf.zhimengjing.common.annotation;

import java.lang.annotation.*;

/**
 * @Title: Log
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.annotation
 * @description: 自定义操作日志注解，用于标记需要记录日志的方法。
 *                  可在 AOP 切面中获取注解信息，实现统一日志记录。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块名称，用于描述日志所属模块
     */
    String module() default "";

    /**
     * 操作内容，用于描述具体操作行为
     */
    String operation() default "";
}