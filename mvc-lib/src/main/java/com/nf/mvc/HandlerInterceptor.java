package com.nf.mvc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface HandlerInterceptor {
    default boolean preHandler(ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }

    default void postHandler(ServletRequest request, ServletResponse response, Object handler) throws Exception {

    }
}
