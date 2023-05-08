package com.nf.mvc;

import com.nf.mvc.mappings.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

public interface HandlerMapping {
    HandlerExecutionChain getHandler(HttpServletRequest req) throws ServletException;

    default String getUri(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        return req.getRequestURI().substring(contextPath.length());
    }

    default String getRequestValue(AnnotatedElement element) {
        return element.isAnnotationPresent(RequestMapping.class) ? element.getDeclaredAnnotation(RequestMapping.class).value() : "";
    }

    default List<HandlerInterceptor> getInterceptors(HttpServletRequest request) {
        return MvcContext.getMvcContext().getCustomInterceptors();
    }
}
