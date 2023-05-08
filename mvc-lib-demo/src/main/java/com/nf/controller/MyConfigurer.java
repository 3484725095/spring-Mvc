package com.nf.controller;

import com.nf.mvc.HandlerMapping;
import com.nf.mvc.Interceptors;
import com.nf.mvc.WebMvcConfigurer;
import com.nf.mvc.mappings.RequestMappingHandlerMapping;
import com.nf.mvc.support.AntPathMatcher;

public class MyConfigurer implements WebMvcConfigurer {

    @Override
    public void configureHandlerMapping(HandlerMapping handlerMapping) {
        if (handlerMapping instanceof RequestMappingHandlerMapping) {
            RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) handlerMapping;
            mapping.setPathMatcher(new AntPathMatcher.Builder().build());
        }
    }
}
