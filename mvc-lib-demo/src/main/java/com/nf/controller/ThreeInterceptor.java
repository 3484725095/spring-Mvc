package com.nf.controller;

import com.nf.mvc.HandlerInterceptor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ThreeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandler(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("three 1111---");
        return false;
    }

    @Override
    public void postHandler(ServletRequest request, ServletResponse response, Object handler) throws Exception {
        System.out.println("three 2222---");
    }
}
