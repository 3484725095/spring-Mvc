package com.nf.mvc.arguments;

import com.nf.mvc.MethodArgumentResolver;
import com.nf.mvc.util.ReflectionUtils;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletRequest;

public class ComplexTypeMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supports(MethodParameter parameter) {
        return ReflectionUtils.isComplexProperty(parameter.getParamType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest req) throws Exception {
        Object instance = ReflectionUtils.newInstance(parameter.getParamType());
        BeanUtils.populate(instance, req.getParameterMap());
        return instance;
    }
}
