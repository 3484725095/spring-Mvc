package com.nf.mvc.adapters;

import com.nf.mvc.HandlerAdapter;
import com.nf.mvc.handler.HandlerClass;
import com.nf.mvc.handler.HandlerMethod;
import com.nf.mvc.HttpRequestHandler;
import com.nf.mvc.ViewResult;
import com.nf.mvc.view.VoidViewResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpRequestHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod && HttpRequestHandler.class.isAssignableFrom(((HandlerClass) handler).getHandlerClass());
    }

    @Override
    public ViewResult handler(HttpServletResponse resp, HttpServletRequest req, Object handler) throws Exception {
//      首先将handler对象转换为HandlerInfo对象。
        HandlerMethod handlerMethod = (HandlerMethod) handler;
//      通过HandlerInfo对象获取其对应的类对象Class，再通过newInstance()方法创建该类的实例对象instance。
        Class<?> handlerInfoClz = handlerMethod.getHandlerClass();
        Object instance = handlerInfoClz.newInstance();
//      将instance对象转换为HttpRequestHandler类型的对象requestHandler。
        HttpRequestHandler httpRequestHandler = (HttpRequestHandler) instance;
//      调用requestHandler对象的processRequest方法，将HttpServletRequest和HttpServletResponse对象作为参数传递进去，以处理客户端请求。
        httpRequestHandler.processRequest(req, resp);
//      最后返回一个VoidView对象作为响应结果。
        return new VoidViewResult();
    }
}
