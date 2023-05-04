package com.nf.mvc.exception;

import com.nf.mvc.HandlerExceptionResolver;
import com.nf.mvc.MvcContext;
import com.nf.mvc.ViewResult;
import com.nf.mvc.handler.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nf.mvc.ViewResult.handleViewResult;
import static com.nf.mvc.util.ExceptionUtils.getRootCause;

public class ExceptionHandlerExceptionResolver implements HandlerExceptionResolver {
    private List<HandlerMethod> exHandlerMethods = new ArrayList<HandlerMethod>();

    public ExceptionHandlerExceptionResolver() {
        scanHandleExMethods();
        sortExHandleMethods();
    }


    @Override
    public ViewResult resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        Exception exposedException = (Exception) getRootCause(ex);
        for (HandlerMethod exHandlerMethod : exHandlerMethods) {
            Method method = exHandlerMethod.getHandlerMethod();
            Class<? extends Exception> exceptionClass = method.getDeclaredAnnotation(ExceptionHandler.class).value();
            if (exceptionClass.isAssignableFrom(exposedException.getClass())) {
                try {
                    Object instance = method.getDeclaringClass().getConstructor().newInstance();
                    return handleViewResult(method.invoke(instance, exposedException));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

    private void sortExHandleMethods() {
        Collections.sort(exHandlerMethods, (m1, m2) -> m1.getHandlerMethod().getDeclaredAnnotation(ExceptionHandler.class).value().isAssignableFrom(m2.getHandlerMethod().getDeclaredAnnotation(ExceptionHandler.class).value()) ? 1 : -1);
    }

    private void scanHandleExMethods() {
        List<Class<?>> classList = MvcContext.getMvcContext().getAllScanedClasses();
        for (Class<?> clz : classList) {
            Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ExceptionHandler.class)) {
                    HandlerMethod handlerMethod = new HandlerMethod(method);
                    exHandlerMethods.add(handlerMethod);
                }
            }
        }
    }
}
