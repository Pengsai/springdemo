package com.ps.spring.demo.mvc.action;

import com.ps.spring.demo.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName TwoAction
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:15
 **/
public class TwoAction {

    private IDemoService iDemoService;

    public void edit(HttpServletRequest req, HttpServletResponse response, String name) {
        String result = iDemoService.get(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
