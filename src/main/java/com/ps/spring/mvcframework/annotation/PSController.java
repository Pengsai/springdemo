package com.ps.spring.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName PSController
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:50
 **/
@Target({ElementType.TYPE})// 注解只能在类上使用
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PSController {
    String value() default "";
}
