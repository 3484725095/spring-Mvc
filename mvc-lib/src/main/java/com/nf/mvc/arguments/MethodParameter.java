package com.nf.mvc.arguments;

import java.lang.reflect.Parameter;

public class MethodParameter {
    private Parameter parameter;
    private String paramName;

    public MethodParameter(Parameter parameter, String paramName) {
        this.parameter = parameter;
        this.paramName = paramName;
    }

    public Class<?> getParameterType() {
        return parameter.getType();
    }

    public Parameter getParameter() {
        return parameter;
    }

    public String getParamName() {
        return paramName;
    }
}
