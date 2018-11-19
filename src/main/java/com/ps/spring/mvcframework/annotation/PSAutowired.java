package com.ps.spring.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName PSAutowired
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:58
 **/

@Target({ElementType.FIELD})// 注解只能在类上使用
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PSAutowired {
    String value() default "";
}

