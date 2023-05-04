package com.nf.mvc.mappings;

import com.nf.mvc.HandlerExecutionChain;
import com.nf.mvc.HandlerInterceptor;
import com.nf.mvc.HandlerMapping;
import com.nf.mvc.MvcContext;
import com.nf.mvc.handler.HandlerMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMappingHandlerMapping implements HandlerMapping {
    private Map<String, HandlerMethod> handlers = new HashMap<>();

    public RequestMappingHandlerMapping() {
        resolveHandlers();
    }

    private void resolveHandlers() {
        List<Class<?>> classList = MvcContext.getMvcContext().getAllScanedClasses();
        for (Class<?> clz : classList) {
            String classValue = getRequestValue(clz);
            for (Method method : clz.getDeclaredMethods()) {
                HandlerMethod handlerMethod = new HandlerMethod(method);
                String methodValue = getRequestValue(method);
                String url = classValue.toLowerCase() + methodValue.toLowerCase();
                handlers.put(url, handlerMethod);
            }
        }
    }

    //TODO:类上面没有注解，通过获取类名和用户在注解上面写的值吻合
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest req) throws ServletException {
        String uri = getUri(req);
        HandlerMethod handler = handlers.get(uri);
        return handler == null ? null : new HandlerExecutionChain(handler, MvcContext.getMvcContext().getCustomInterceptors());
    }
}
