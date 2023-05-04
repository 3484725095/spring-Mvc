package com.nf.mvc.mappings;

import com.nf.mvc.HandlerExecutionChain;
import com.nf.mvc.HandlerInterceptor;
import com.nf.mvc.handler.HandlerClass;
import com.nf.mvc.handler.HandlerMethod;
import com.nf.mvc.HandlerMapping;
import com.nf.mvc.MvcContext;
import com.nf.mvc.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过截取后的方法和地址找到对应的处理器（handler）
 */
public class NameConventionHandlerMapping implements HandlerMapping {
    private Map<String, HandlerClass> handlers = new HashMap<>();

    private static final String SUFFIX = "Controller";

    public NameConventionHandlerMapping() {
        //通过类图扫描得到所有的类信息
        List<Class<?>> classInfos = MvcContext.getMvcContext().getAllScanedClasses();
        //通过classInfos对所有类进行一个遍历
        for (Class<?> classInfo : classInfos) {
            //获取到每个类最短的名字,比如firstController
            String simpleName = classInfo.getSimpleName();
            //对所有的类进行一个结尾判断
            if (simpleName.endsWith(SUFFIX)) {
                //如果是则根据类名生成对应的uri
                String uri = UriUtils.generateHandleUrl(simpleName);
                //根据类信息创建一个handlerInfo对象，封装该类的信息
                HandlerClass handlerClass = new HandlerClass(classInfo);
                //把对应uri进行大小写的一个处理，并和handlerInfo对象放进map里面
                handlers.put(uri.toLowerCase(), handlerClass);
            }
        }
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest req) throws ServletException {
        String uri = getUri(req);
        Object handler = handlers.get(uri);
        return handler == null ? null : new HandlerExecutionChain(handler, MvcContext.getMvcContext().getCustomInterceptors());
    }
}
