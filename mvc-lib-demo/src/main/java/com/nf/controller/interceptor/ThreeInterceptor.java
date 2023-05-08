package com.nf.controller.interceptor;

import com.nf.mvc.HandlerInterceptor;
import com.nf.mvc.Interceptors;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Interceptors(excludePattern = {"/four/list"})
public class ThreeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandler(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("three 1111---");
        return true;
    }

    @Override
    public void postHandler(ServletRequest request, ServletResponse response, Object handler) throws Exception {
        System.out.println("three 2222---");
    }
}
