package com.nf.controller;

import com.nf.mvc.HandlerInterceptor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecondInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandler(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("second 1111---");
        return true;
    }

    @Override
    public void postHandler(ServletRequest request, ServletResponse response, Object handler) throws Exception {
        System.out.println("second 2222---");
    }
}