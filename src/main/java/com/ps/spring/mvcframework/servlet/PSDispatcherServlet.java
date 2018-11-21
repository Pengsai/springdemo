package com.ps.spring.mvcframework.servlet;

import com.ps.spring.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    private Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        try {
            doDispacher(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail: ");
        }

    }

    private void doDispacher(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        // 先拿到用户的请求
        String url = req.getRequestURI();

        String contextPath = req.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", "/");

        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 NOT FOUND :" + url);
            return;
        }

        Method method = this.handlerMapping.get(url);

        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
        Object o = ioc.get(beanName);

        Map<String, String[]> parameterMap = req.getParameterMap();

        method.invoke(o, new Object[]{req, resp, parameterMap.get("name")[0]});

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        // 1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));


        // 2. 扫描并加载相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3. 初始化IOC容器
        doInstance();

        // 4. 反射依赖注入(自动赋值)
        doAutowired();

        // 5. 构造HandlerMapping,将url和Method建立一对一的关系
        initHandlerMapping();

        System.out.println("PS spring mvc is init");
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Class<?> clazz = entry.getValue().getClass();

            if (!clazz.isAnnotationPresent(PSController.class)) {
                continue;
            }

            String baseUrl = "";
            if (clazz.isAnnotationPresent(PSRequestMapping.class)) {
                PSRequestMapping requestMapping = clazz.getAnnotation(PSRequestMapping.class);
                baseUrl = requestMapping.value();

            }

            Method[] methods = clazz.getMethods();

            for (Method method : methods) {


                if (!method.isAnnotationPresent(PSRequestMapping.class)) {
                    continue;
                }

                PSRequestMapping requestMapping = method.getAnnotation(PSRequestMapping.class);

                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");

                handlerMapping.put(url, method);

                System.out.println("Mapped: " + url + "," + method);

            }


        }
    }

    private void doAutowired() {

        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            //getDeclaredFields()获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (!field.isAnnotationPresent(PSAutowired.class)) {
                    continue;
                }

                PSAutowired annotation = field.getAnnotation(PSAutowired.class);

                String beanName = annotation.value();

                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }

                // 如果private protected default 暴力访问
                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    private void doInstance() {

        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {

                className = className.replaceAll("/", "\\.");
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(PSController.class)) {

                    Object instance = clazz.newInstance();

                    String beanName = lowerFirstCase(clazz.getSimpleName());

                    ioc.put(beanName, instance);

                } else if (clazz.isAnnotationPresent(PSService.class)) {

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

                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The beanName " + clazz.getName() + " is aleardy exists!!!");
                        }

                        ioc.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String lowerFirstCase(String simpleName) {

        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        scanPackage = scanPackage.replaceAll("\\.", "/");
        URL url = this.getClass().getClassLoader().getResource(scanPackage);
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {

            if (file.isDirectory()) {
                doScanner(scanPackage + "/" + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "/" + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }

    }

    private void doLoadConfig(String contextConfigLocation) {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);

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
