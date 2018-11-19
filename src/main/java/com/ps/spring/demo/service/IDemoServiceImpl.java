package com.ps.spring.demo.service;

/**
 * @ClassName DemoServiceImpl
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:08
 **/
public class IDemoServiceImpl implements IDemoService {

    public String get(String name) {
        return "My name is" + name;
    }

}
