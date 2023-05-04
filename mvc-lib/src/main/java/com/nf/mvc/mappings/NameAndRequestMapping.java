package com.nf.mvc.mappings;

import com.nf.mvc.HandlerExecutionChain;
import com.nf.mvc.HandlerInterceptor;
import com.nf.mvc.HandlerMapping;
import com.nf.mvc.MvcContext;
import com.nf.mvc.handler.HandlerClass;
import com.nf.mvc.handler.HandlerMethod;
import com.nf.mvc.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameAndRequestMapping implements HandlerMapping {
    private Map<String, HandlerMethod> handlers = new HashMap<>();
    private static final String SUFFIX = "Controller";

    public NameAndRequestMapping() {
        resolveHandlers();
    }

    private void resolveHandlers() {
        String urlPrefix = "";
        List<Class<?>> classList = MvcContext.getMvcContext().getAllScanedClasses();
        for (Class<?> clz : classList) {
            if (!clz.isAnnotationPresent(RequestMapping.class)) {
                String simpleName = clz.getSimpleName();
                if (simpleName.endsWith(SUFFIX)) {
                    urlPrefix = UriUtils.generateHandleUrl(simpleName).toLowerCase();
                }
                for (Method method : clz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        HandlerMethod handlerMethod = new HandlerMethod(method);
                        String uri = urlPrefix + getRequestValue(method).toLowerCase();
                        handlers.put(uri, handlerMethod);
                    }
                }
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
