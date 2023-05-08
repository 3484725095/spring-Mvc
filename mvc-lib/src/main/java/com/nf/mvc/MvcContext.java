package com.nf.mvc;

import com.nf.mvc.support.OrderComparator;
import com.nf.mvc.util.ReflectionUtils;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MvcContext {
    private static final MvcContext instance = new MvcContext();
    private static final OrderComparator orderComparator = new OrderComparator();

    private ScanResult scanResult;
    private List<HandlerMapping> handlerMappings = new ArrayList<>();
    private List<HandlerAdapter> handlerAdapters = new ArrayList<>();
    private List<MethodArgumentResolver> argumentResolvers = new ArrayList<>();
    private List<HandlerExceptionResolver> exceptionResolvers = new ArrayList<>();
    private List<WebMvcConfigurer> webMvcConfigurers = new ArrayList<>();
    private List<Class<?>> allScanedClasses = new ArrayList<>();


    private List<HandlerMapping> customHandlerMappings = new ArrayList<>();
    private List<HandlerAdapter> customHandlerAdapters = new ArrayList<>();
    private List<MethodArgumentResolver> customArgumentResolvers = new ArrayList<>();
    private List<HandlerExceptionResolver> customExceptionResolvers = new ArrayList<>();
    private List<HandlerInterceptor> customInterceptors = new ArrayList<>();


    public MvcContext() {
    }

    public static MvcContext getMvcContext() {
        return instance;
    }

    /**
     * 根据类图扫描得结果进行一个分析，然后把每个对应的类添加集合中去，以便后续使用
     * <p>
     * 设置成public修饰符，框架使用者是可以直接修改这个扫描
     * 影响就不是很好，所以就改成default修饰符，只能在本包
     * 或子包中访问，基本上就是mvc框架内可以访问
     * 我设计的mvc框架，是有以下几个扩展点
     * DispatcherServlet
     * HandlerMapping
     * HandlerAdapter
     * MethodArgumentResolver
     * HandlerExceptionResolver
     * ViewResult
     * <p>
     * 目前扫描的是各种各样，有HandlerMapping，也有HandlerAdapter
     * 也有Handler
     * 因为一般不会写一个类，实现多个接口，所以这种多个if写法问题不大
     *
     * @param scanResult
     */
    void config(ScanResult scanResult) {
        this.scanResult = scanResult;
        ClassInfoList allClasses = scanResult.getAllClasses();
        for (ClassInfo classInfo : allClasses) {
            Class<?> scanedClass = classInfo.loadClass();
            resolveMvcClass(scanedClass);
            allScanedClasses.add(scanedClass);
        }
    }

    private void resolveMvcClass(Class<?> scanedClass) {
        resolveClasses(scanedClass, HandlerMapping.class, customHandlerMappings);
        resolveClasses(scanedClass, HandlerAdapter.class, customHandlerAdapters);
        resolveClasses(scanedClass, MethodArgumentResolver.class, customArgumentResolvers);
        resolveClasses(scanedClass, HandlerExceptionResolver.class, customExceptionResolvers);
        resolveClasses(scanedClass, HandlerInterceptor.class, customInterceptors);
        resolveClasses(scanedClass, WebMvcConfigurer.class, webMvcConfigurers);
    }

    private <T> void resolveClasses(Class<?> scannedClass, Class<? extends T> mvcInf, List<T> list) {
        if (mvcInf.isAssignableFrom(scannedClass)) {
            T instance = (T) ReflectionUtils.newInstance(scannedClass);
            list.add(instance);
        }
    }

    public List<HandlerMapping> getCustomHandlerMappings() {
        Collections.sort(customHandlerMappings, orderComparator);
        return Collections.unmodifiableList(customHandlerMappings);
    }

    public List<HandlerAdapter> getCustomHandlerAdapters() {
        Collections.sort(customHandlerAdapters, orderComparator);
        return Collections.unmodifiableList(customHandlerAdapters);
    }

    public List<MethodArgumentResolver> getCustomArgumentResolvers() {
        Collections.sort(customArgumentResolvers, orderComparator);
        return Collections.unmodifiableList(customArgumentResolvers);
    }

    public List<HandlerExceptionResolver> getCustomExceptionResolvers() {
        Collections.sort(customExceptionResolvers, orderComparator);
        return Collections.unmodifiableList(customExceptionResolvers);
    }

    public List<HandlerMapping> getHandlerMappings() {
        return Collections.unmodifiableList(handlerMappings);
    }

    public List<HandlerAdapter> getHandlerAdapters() {
        return Collections.unmodifiableList(handlerAdapters);
    }

    public List<MethodArgumentResolver> getArgumentResolvers() {
        return Collections.unmodifiableList(argumentResolvers);
    }

    public List<HandlerExceptionResolver> getExceptionResolvers() {
        return Collections.unmodifiableList(exceptionResolvers);
    }

    public List<HandlerInterceptor> getCustomInterceptors() {
        Collections.sort(customInterceptors, orderComparator);
        return Collections.unmodifiableList(customInterceptors);
    }

    public WebMvcConfigurer getWebMvcConfigurers() {
        if (webMvcConfigurers.size() > 1) {
            throw new IllegalStateException("配置器应该只写一个");
        }
        if(webMvcConfigurers.size()==0){
            return new WebMvcConfigurer(){};
        }

        return webMvcConfigurers.get(0);
    }


    // 以下四个方法是默认修饰符，主要是在框架内调用，用户不能调用
    void setHandlerMappings(List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    void setHandlerAdapters(List<HandlerAdapter> handlerAdapters) {
        this.handlerAdapters = handlerAdapters;
    }

    void setArgumentResolvers(List<MethodArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    void setExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        this.exceptionResolvers = exceptionResolvers;
    }

    public List<Class<?>> getAllScanedClasses() {
        return Collections.unmodifiableList(allScanedClasses);
    }


}
