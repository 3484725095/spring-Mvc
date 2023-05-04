package com.nf.mvc.exception;

import com.nf.mvc.HandlerExceptionResolver;
import com.nf.mvc.ViewResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ViewResult resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(ex instanceof RuntimeException)) {
            return null;
        }
        //我们这个解析器只解析RuntimeException
        System.out.println("异常消息是:" + ex.getMessage());
        //下面这行代码不用写，只是演示用
        return null;
    }
}
