package com.ps.spring.mvcframework.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @ClassName PSDispatcherServlet
 * @Description TODO
 * @Author PS
 * @Date 2018/11/19 16:23
 **/
public class PSDispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doDispacher();

    }

    private void doDispacher() {

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        // 1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLoaction"));


        // 2. 扫描并加载相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3. 初始化IOC容器
        doInstance();

        // 4. 反射依赖注入(自动赋值)
        doAutowired();

        // 5. 构造HandlerMapping,将url和Method建立一对一的关系
        initHandlerMapping();
    }

    private void initHandlerMapping() {

    }

    private void doAutowired() {

    }

    private void doInstance() {

        if (classNames.isEmpty()) {
            return;
        }

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {

            if (file.isDirectory()) {
                doScanner(scanPackage+ "."+file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage+"."+file.getName().replace(".class", "");
                classNames.add(className);
            }
        }

    }

    private void doLoadConfig(String contextConfigLoaction) {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLoaction);

        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
