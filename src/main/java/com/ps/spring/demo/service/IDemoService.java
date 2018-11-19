package com.ps.spring.demo.service;

import com.ps.spring.mvcframework.annotation.PSService;

/**
 * @InterfaceName DemoService
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:08
 **/

@PSService
public interface IDemoService {

    String get(String name);

}
