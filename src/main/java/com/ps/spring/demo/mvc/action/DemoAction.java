package com.ps.spring.demo.mvc.action;

import com.ps.spring.demo.service.IDemoService;
import com.ps.spring.demo.service.IDemoService2;
import com.ps.spring.mvcframework.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName DemoAction
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:07
 **/
@PSController
@PSRequestMapping("/demo")
public class DemoAction {

    @PSAutowired
    private IDemoService iDemoService;

    @PSAutowired
    private IDemoService2 iDemoService2;

    @PSRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse response,
                      @PSRequestParam("name") String name) {
        String result = iDemoService.get(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PSRequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse response,
                    @PSRequestParam("a")Integer a, @PSRequestParam("b") Integer b) {
        try {
            response.getWriter().write(a + "+" + b + "=" + (a + b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PSRequestMapping("/remove")
    public void remove(HttpServletRequest req, HttpServletResponse response,
                       Integer id) {

    }

}
