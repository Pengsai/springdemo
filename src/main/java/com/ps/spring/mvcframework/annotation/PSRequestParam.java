package com.ps.spring.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName PSRequestParam
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 17:02
 **/
@Target({ElementType.PARAMETER})// 注解只能在类上使用
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PSRequestParam {
    String value() default "";
}
