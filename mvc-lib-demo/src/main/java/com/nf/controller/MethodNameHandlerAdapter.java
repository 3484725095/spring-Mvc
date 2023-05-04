package com.nf.controller;

import com.nf.mvc.HandlerAdapter;
import com.nf.mvc.handler.HandlerMethod;
import com.nf.mvc.ViewResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 此HandlerAdapter就是看handler内部有没有一个叫process名字的方法
 * 有就支持，没有就不管
 */
public class MethodNameHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> handlerClass = handlerMethod.getHandlerClass();
            try {
                Method method = handlerClass.getDeclaredMethod("process");
                return method != null;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public ViewResult handler(HttpServletResponse resp, HttpServletRequest req, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Class<?> handlerClass = handlerMethod.getHandlerClass();

        Object instance = handlerClass.newInstance();
        Method method = handlerClass.getDeclaredMethod("process");
        return (ViewResult) method.invoke(instance);

    }
}
