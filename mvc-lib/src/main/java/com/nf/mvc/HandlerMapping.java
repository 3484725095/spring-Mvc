package com.nf.mvc;

import com.nf.mvc.mappings.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;

public interface HandlerMapping {
    HandlerExecutionChain getHandler(HttpServletRequest req) throws ServletException;

    default String getUri(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        return req.getRequestURI().substring(contextPath.length()).toLowerCase();
    }

    default String getRequestValue(AnnotatedElement element) {
        return element.isAnnotationPresent(RequestMapping.class) ? element.getDeclaredAnnotation(RequestMapping.class).value().toLowerCase() : "";
    }
}
