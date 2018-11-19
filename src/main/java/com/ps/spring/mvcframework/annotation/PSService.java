package com.ps.spring.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @InterfaceName PSService
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 17:00
 **/

@Target({ElementType.TYPE})// 注解只能在类上使用
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PSService {
    String value() default "";
}

