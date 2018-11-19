package com.ps.spring.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName PSRequestMapping
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:54
 **/

@Target({ElementType.TYPE, ElementType.METHOD})// 注解只能在类上使用
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PSRequestMapping {
    String value() default "";
}
