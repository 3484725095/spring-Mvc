package com.nf.mvc.mappings;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nf.mvc.*;
import com.nf.mvc.handler.HandlerMethod;
import com.nf.mvc.support.AntPathMatcher;
import com.nf.mvc.support.PathMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

public class RequestMappingHandlerMapping implements HandlerMapping {

    private static final PathMatcher defaultPathMatcher = new AntPathMatcher.Builder().build();
    private Map<String, HandlerMethod> handlers = new HashMap<>();
    private PathMatcher pathMatcher = new AntPathMatcher.Builder().build();

    Cache<String, HandlerExecutionChain> cache = Caffeine.newBuilder().initialCapacity(10).maximumSize(100).build();


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
                String url = classValue + methodValue;
                handlers.put(url, handlerMethod);
            }
        }
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest req) throws ServletException {
        String uri = getUri(req);
        HandlerExecutionChain chain = cache.get(uri, k -> {
            HandlerMethod handler = getHandlerInternal(uri);
            if (handler != null) {
                return new HandlerExecutionChain(handler, getInterceptors(req));
            }
            return null;
        });
        return chain;
    }

    @Override
    public List<HandlerInterceptor> getInterceptors(HttpServletRequest request) {
        List<HandlerInterceptor> result = new ArrayList<>();
        List<HandlerInterceptor> interceptors = MvcContext.getMvcContext().getCustomInterceptors();
        String requestUrl = getUri(request);
        for (HandlerInterceptor interceptor : interceptors) {
            Class<? extends HandlerInterceptor> interceptorClass = interceptor.getClass();
            if (interceptorClass.isAnnotationPresent(Interceptors.class)) {
                Interceptors annotation = interceptorClass.getDeclaredAnnotation(Interceptors.class);
                String[] includesPattern = annotation.value();
                String[] excludesPattern = annotation.excludePattern();
                if (shouldApply(requestUrl, includesPattern) == true && shouldApply(requestUrl, excludesPattern) == false) {
                    result.add(interceptor);
                }
            } else {
                //没有注解修饰的拦截器被认为是拦截所有的请求，完全不理会当前请求url是什么
                result.add(interceptor);
            }
        }
        return result;
    }

    protected boolean shouldApply(String requestUrl, String... patterns) {
        boolean shouldApply = false;
        if (patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            shouldApply = getPathMatcher().isMatch(pattern, requestUrl);
            if (shouldApply) {
                break;
            }
        }
        return shouldApply;
    }

    private HandlerMethod getHandlerInternal(String uri) {
        HandlerMethod handler = null;
        Set<String> keys = handlers.keySet();
        for (String key : keys) {
            if (getPathMatcher().isMatch(key, uri)) {
                handler = handlers.get(key);
            }
        }
        return handler;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }
}
