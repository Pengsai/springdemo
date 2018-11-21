package com.ps.spring.demo.service;

import com.ps.spring.mvcframework.annotation.PSService;

/**
 * @ClassName DemoServiceImpl
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:08
 **/
@PSService
public class IDemoServiceImpl implements IDemoService {

    public String get(String name) {
        return "My name is " + name;
    }

}
