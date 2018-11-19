package com.ps.spring.mvcframework.servlet;

import com.ps.spring.mvcframework.annotation.PSController;
import com.ps.spring.mvcframework.annotation.PSService;

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

                if (clazz.isAnnotationPresent(PSController.class)) {

                    Object instance = clazz.newInstance();

                    String beanName = lowerFirstCase(clazz.getSimpleName());

                    ioc.put(beanName, instance);

                } else if (clazz.isAnnotationPresent(PSService.class)){

                    // service 初始化的并不是类本身 如果是接口的话 而是类对应的实现类
                    // 1. 默认就是类名的首字母小写作为key

                    PSService service = clazz.getAnnotation(PSService.class);
                    String beanName = service.value();

                    // 2. 用户自定义beanName 那么要优先使用自定义的beanName

                    if ("".equals(beanName)) {
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    // 3. 默认的对象时接口， 我们要使用
                    // 用接口的全类名作为key， 实现类作为值， 方便依赖注入时使用

                    Class<?>[] interfaces = clazz.getInterfaces();

                    for (Class<?> i : interfaces) {
                        ioc.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    private String lowerFirstCase(String simpleName) {

        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
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
