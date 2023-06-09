package com.nf.mvc;

import com.nf.mvc.adapters.HttpRequestHandlerAdapter;
import com.nf.mvc.adapters.RequestMappingHandlerAdapter;
import com.nf.mvc.arguments.*;
import com.nf.mvc.exception.ExceptionHandlerExceptionResolver;
import com.nf.mvc.mappings.NameAndRequestMapping;
import com.nf.mvc.mappings.NameConventionHandlerMapping;
import com.nf.mvc.mappings.RequestMappingHandlerMapping;
import com.nf.mvc.support.HttpHeaders;
import com.nf.mvc.support.HttpMethod;
import com.nf.mvc.util.CorsUtils;
import com.nf.mvc.util.ScanUtils;
import com.nf.mvc.util.StringUtils;
import io.github.classgraph.ScanResult;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 总控制器，负责分发任务，找到相应的处理器
 */
public class DispatcherServlet extends HttpServlet {
    private static final String BASE_PACKAGE = "base-package";
    private List<HandlerMapping> handlerMappings = new ArrayList<>();
    private List<HandlerAdapter> handlerAdapters = new ArrayList<>();
    private List<MethodArgumentResolver> argumentResolvers = new ArrayList<>();
    private List<HandlerExceptionResolver> exceptionResolvers = new ArrayList<>();
    private final CorsConfiguration corsConfiguration = CorsConfiguration.defaultInstance();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //通过xml参数获取包名
        String getPackage = getBasePackage(config);
        //通过包名获取查询的结果
        ScanResult scanResult = ScanUtils.scan(getPackage);
        //通过初始化获取每个类对应的结果mapping
        initMvcContext(scanResult);
        initMvc();
        configMvc();
    }

    private void initMvc() {
        initArgumentResolvers();
        //获取到所有的mapping进行初始化，只执行一次
        initHandlerMapping();
        //获取到所有的Adapter进行初始化，只执行一次
        initHandlerAdapter();
        initExceptionResolvers();
    }

    private void configMvc() {
        MvcContext mvcContext = MvcContext.getMvcContext();
        WebMvcConfigurer mvcConfigurer = mvcContext.getWebMvcConfigurers();
        if (mvcConfigurer == null) {
            return;
        }

        //由于这些配置方法子类可以重写，所以给这些方法都设置了参数，便于重写，是可以不用添加任何参数的
        configArgumentResolvers(MvcContext.getMvcContext().getArgumentResolvers(), mvcConfigurer);
        configHandlerMappings(MvcContext.getMvcContext().getHandlerMappings(), mvcConfigurer);
        configHandlerAdapters(MvcContext.getMvcContext().getHandlerAdapters(), mvcConfigurer);
        configExceptionResolvers(MvcContext.getMvcContext().getExceptionResolvers(), mvcConfigurer);
        //由于corsConfiguration对象是有了默认值设置的实例，没有配置器的时候不配置cors也能用默认设置处理跨域
        configGlobalCors(this.corsConfiguration, mvcConfigurer);
    }

    protected void configGlobalCors(CorsConfiguration configuration, WebMvcConfigurer mvcConfigurer) {
        // 先设定默认设置，如果用户不需要这些默认设置，可以调用clearDefaultConfiguration方法进行清除
        configuration.applyDefaultConfiguration();
        mvcConfigurer.configureCors(configuration);
        // mvcConfigurer.configureCors(configuration) 这行代码你也可以换成像下面这样写
        // executeMvcComponentsConfig(Arrays.asList(configuration),mvcConfigurer::configureCors);
    }

    protected void configArgumentResolvers(List<MethodArgumentResolver> argumentResolvers, WebMvcConfigurer mvcConfigurer) {
        executeMvcComponentsConfig(argumentResolvers, mvcConfigurer::configureArgumentResolver);
    }

    protected void configHandlerMappings(List<HandlerMapping> handlerMappings, WebMvcConfigurer mvcConfigurer) {
        executeMvcComponentsConfig(handlerMappings, mvcConfigurer::configureHandlerMapping);
    }

    protected void configHandlerAdapters(List<HandlerAdapter> handlerAdapters, WebMvcConfigurer mvcConfigurer) {
        executeMvcComponentsConfig(handlerAdapters, mvcConfigurer::configureHandlerAdapter);
    }

    protected void configExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers, WebMvcConfigurer mvcConfigurer) {
        executeMvcComponentsConfig(exceptionResolvers, mvcConfigurer::configureExceptionResolver);
    }

    private <T> void executeMvcComponentsConfig(List<T> mvcComponents, Consumer<T> consumer) {
        mvcComponents.forEach(consumer);
    }

    /**
     * 这个是优化后的，在作者层面上可能会比较方便，在用户的层面用多个就不方便和初始的差不多
     *
     * @param configs：要配置的参数集合
     * @param clz：接口的实现类
     * @param <T>
     */
    @Deprecated
    private <T> void configure(List<T> configs, Class<T> clz) {
        MvcContext mvcContext = MvcContext.getMvcContext();
        WebMvcConfigurer webMvcConfigurers = mvcContext.getWebMvcConfigurers();
        for (T config : configs) {
            if (clz.isInstance(config)) {
                webMvcConfigurers.configureHandler(config);
            }
        }
    }


    private void initMvcContext(ScanResult scanResult) {
        MvcContext.getMvcContext().config(scanResult);
    }

    private void initArgumentResolvers() {
        List<MethodArgumentResolver> customArgumentResolvers = getCustomArgumentResolvers();

        List<MethodArgumentResolver> defaultArgumentResolvers = getDefaultArgumentResolvers();

        argumentResolvers.addAll(customArgumentResolvers);
        argumentResolvers.addAll(defaultArgumentResolvers);
        //把定制+默认的所有HandlerMapping组件添加到上下文中
        MvcContext.getMvcContext().setArgumentResolvers(argumentResolvers);
    }

    private List<MethodArgumentResolver> getCustomArgumentResolvers() {
        return MvcContext.getMvcContext().getCustomArgumentResolvers();
    }

    protected List<MethodArgumentResolver> getDefaultArgumentResolvers() {
        List<MethodArgumentResolver> argumentResolvers = new ArrayList<>();
        argumentResolvers.add(new RequestBodyMethodArgumentResolver());
        argumentResolvers.add(new ServletApiMethodArgumentResolver());
        argumentResolvers.add(new MultipartFileMethodArgumentResolver());
        argumentResolvers.add(new SimpleTypeMethodArgumentResolver());
        argumentResolvers.add(new ComplexTypeMethodArgumentResolver());

        return argumentResolvers;
    }

    /**
     * 把用户自定义的mapping和框架中的mapping放在一个集合中
     */
    private void initHandlerMapping() {
        List<HandlerMapping> customHandlerMappings = getCustomHandlerMappings();
        List<HandlerMapping> defaultHandlerMappings = getDefaultHandlerMappings();
        handlerMappings.addAll(customHandlerMappings);
        handlerMappings.addAll(defaultHandlerMappings);
        MvcContext.getMvcContext().setHandlerMappings(handlerMappings);
    }

    /**
     * 把用户自定义的mapping添加集合中
     *
     * @return
     */
    protected List<HandlerMapping> getCustomHandlerMappings() {
        return MvcContext.getMvcContext().getCustomHandlerMappings();
    }

    /**
     * 把自己框架中的所有mapping添加到集合中
     *
     * @return
     */
    protected List<HandlerMapping> getDefaultHandlerMappings() {
        List<HandlerMapping> mappings = new ArrayList<>();
        mappings.add(new RequestMappingHandlerMapping());
        mappings.add(new NameAndRequestMapping());
        mappings.add(new NameConventionHandlerMapping());
        return mappings;
    }

    /**
     * 把用户自定义的mapping和框架中的adapter放在一个集合中
     */
    private void initHandlerAdapter() {
        List<HandlerAdapter> customHandlerAdapters = getCustomHandlerAdapters();
        List<HandlerAdapter> defaultHandlerAdapters = getDefaultHandlerAdapters();
        handlerAdapters.addAll(customHandlerAdapters);
        handlerAdapters.addAll(defaultHandlerAdapters);
        MvcContext.getMvcContext().setHandlerAdapters(handlerAdapters);
    }

    /**
     * 把用户自定义的adapter添加集合中
     *
     * @return
     */
    protected List<HandlerAdapter> getCustomHandlerAdapters() {
        return MvcContext.getMvcContext().getCustomHandlerAdapters();
    }

    /**
     * 把自己框架中的所有adapter添加到集合中
     *
     * @return
     */
    protected List<HandlerAdapter> getDefaultHandlerAdapters() {
        List<HandlerAdapter> adapters = new ArrayList<>();
        adapters.add(new RequestMappingHandlerAdapter());
        adapters.add(new HttpRequestHandlerAdapter());
        return adapters;
    }


    private void initExceptionResolvers() {
        List<HandlerExceptionResolver> customExceptionResolvers = getCustomExceptionResolvers();
        List<HandlerExceptionResolver> defaultExceptionResolvers = getDefaultExceptionResolvers();
        exceptionResolvers.addAll(customExceptionResolvers);
        exceptionResolvers.addAll(defaultExceptionResolvers);
        MvcContext.getMvcContext().setExceptionResolvers(exceptionResolvers);
    }

    private List<HandlerExceptionResolver> getCustomExceptionResolvers() {
        return MvcContext.getMvcContext().getCustomExceptionResolvers();
    }

    protected List<HandlerExceptionResolver> getDefaultExceptionResolvers() {
        List<HandlerExceptionResolver> resolvers = new ArrayList<>();
        resolvers.add(new ExceptionHandlerExceptionResolver());
        return resolvers;
    }

    private String getBasePackage(ServletConfig config) {
        String pkg = config.getInitParameter(BASE_PACKAGE);

        if (pkg == null || pkg.isEmpty()) {
            throw new IllegalStateException("必须指定扫描的包，此包是控制器或者是其它扩展组件所在的包---");
        }
        return pkg;
    }


    /**
     * 重写Servlet的service方法，并写成自己的逻辑
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setEncoding(req, resp);
        if (CorsUtils.isCorsRequest(req)) {
            processCors(req, resp, corsConfiguration);
            /*如果是预检请求需要return，以便及时响应预检请求，以便处理后续的真正请求*/
            if (CorsUtils.isPreFlightRequest(req)) {
                return;
            }
        }
        doService(req, resp);
    }

    private void doService(HttpServletRequest req, HttpServletResponse resp) {
        HandlerExecutionChain chain;
        HandlerContext handlerContext = HandlerContext.getContext();
        handlerContext.setRequest(req).setResponse(resp);
        //从请求对象（req）中获取处理器（handler）
        try {
            chain = getHandler(req);
            if (chain != null) {
                doDispatch(req, resp, chain);
            } else {
                //这是对一个未找到的错误进行了一个处理，并直接结束
                noHandlerFound(req, resp);
            }
        } catch (Throwable ex) {
            //spring mvc在这个地方是做了额外的异常处理的
            //下面的代码不应该这些写printStackTrace，这里写上主要是我们开发测试用的
            System.out.println("可以在这里再做一层异常处理，比如处理视图渲染方面的异常等，但现在什么都没做,异常消息是:" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            handlerContext.clear();
        }
    }

    protected void doDispatch(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain chain) throws Throwable {
        ViewResult viewResult;
        try {
            if (!chain.applyPreHandle(req, resp)) {
                chain.applyPostHandle(req, resp);
                return;
            }
            Object handler = chain.getHandler();
            HandlerAdapter adapter = getHandlerAdapter(handler);
            viewResult = adapter.handler(resp, req, handler);

            chain.applyPostHandle(req, resp);

        } catch (Exception ex) {
            //这里只处理Exception，非Exception并没有处理，会继续抛出给doService处理
            //这个异常处理也只是处理了Handler整个执行层面的异常，
            // 视图渲染层面的异常是没有处理的，要处理的话可以在doService方法里处理
            viewResult = resolveException(req, resp, chain.getHandler(), ex);
        }
        render(req, resp, viewResult);
    }

    protected ViewResult resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) throws Exception {
        for (HandlerExceptionResolver exceptionResolver : exceptionResolvers) {
            Object result = exceptionResolver.resolveException(req, resp, handler, ex);
            if (result != null) {
                return (ViewResult) result;
            }
        }
        /*表示没有一个异常解析器可以处理异常，那么就应该把异常继续抛出*/
        throw ex;
    }

    protected void setEncoding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }

    protected void noHandlerFound(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //我们获取容器中本来就有的默认servlet来处理静态资源
        //容器中默认servlet是有能力处理静态资源
        //默认servlet的名字，在很多容器中就是叫default，但有些容器不叫default
        //常用的tomcat，jetty这些容器中就是叫default
        req.getServletContext().getNamedDispatcher("default").forward(req, resp);
    }

    /**
     * 渲染视图
     *
     * @param req
     * @param resp
     * @param viewResult
     */
    private static void render(HttpServletRequest req, HttpServletResponse resp, ViewResult viewResult) throws Exception {
        viewResult.render(req, resp);
    }

    protected HandlerExecutionChain getHandler(HttpServletRequest request) {

        try {
            for (HandlerMapping mapping : handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("针对此请求查找handler的时候出错");
        }
        return null;
    }

    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        for (HandlerAdapter handlerAdapter : handlerAdapters) {
            if (handlerAdapter.supports(handler)) {
                return handlerAdapter;
            }
        }
        throw new ServletException("此Handler没有对应的adapter去处理，请在DispatcherServlet中进行额外的配置");
    }

    /**
     * 这里没有用到cors配置，纯粹的直接允许跨域请求
     *
     * @param req
     * @param resp
     * @param configuration
     */
    protected void processCors(HttpServletRequest req, HttpServletResponse resp, CorsConfiguration configuration) {

        String requestOrigin = req.getHeader(HttpHeaders.ORIGIN);
        String allowOrigin = configuration.checkOrigin(requestOrigin);
        if (allowOrigin == null) {
            rejectRequest(resp);
            return;
        }
        //设置允许跨域请求的源
        resp.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin);
        resp.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(configuration.getAllowCredentials()));

        if (HttpMethod.OPTIONS.matches(req.getMethod())) {
            // 浏览器缓存预检请求结果时间,单位:秒
            resp.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, Long.toString(configuration.getMaxAge()));
            // 允许浏览器在预检请求成功之后发送的实际请求方法名，
            // 在MDN中只说要用逗号分隔即可，https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Methods
            // 但其举的例子是逗号后有一个空格，spring的HttpHeaders类的toCommaDelimitedString也是这样的
            resp.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, StringUtils.toCommaDelimitedString(configuration.getAllowedMethods(), ", "));
            // 允许浏览器发送的请求消息头
            resp.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, StringUtils.toCommaDelimitedString(configuration.getAllowedHeaders(), ", "));

        }
    }

    protected void rejectRequest(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getOutputStream().write("Invalid CORS request".getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        } catch (IOException e) {
            throw new IllegalStateException("跨域处理失败");
        }
    }
}
