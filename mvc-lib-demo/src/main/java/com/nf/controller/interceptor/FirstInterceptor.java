package com.nf.controller.interceptor;

import com.nf.mvc.HandlerInterceptor;
import com.nf.mvc.Interceptors;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Interceptors({"/four/insert"})
public class FirstInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandler(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("first 1111---");
        return true;
    }

    @Override
    public void postHandler(ServletRequest request, ServletResponse response, Object handler) throws Exception {
        System.out.println("first 2222-----");
    }
}
