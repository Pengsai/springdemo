package com.ps.spring.demo.service;

/**
 * @ClassName IDemoService2Impl
 * @Description TODO
 * @Author PS
 * @Date 2018/11/20 11:59
 **/
public class IDemoService2Impl implements IDemoService2 {
    @Override
    public String hello(String name) {
        return "My name is" + name;
    }
}
