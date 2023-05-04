package com.nf.mvc.arguments;

import com.nf.mvc.MethodArgumentResolver;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.util.ReflectionUtils;
import com.nf.mvc.util.WebTypeConverterUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class SimpleTypeMethodArgumentResolver implements MethodArgumentResolver {

    @Override
    public boolean supports(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        if (ReflectionUtils.isSimpleProperty(parameterType)) {
            return true;
        }
        if (ReflectionUtils.isAssignableToAny(parameterType, Set.class, List.class)) {
            Class<?> genericType = getGenericType(parameter);
            return ReflectionUtils.isSimpleType(genericType);
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest req) throws Exception {
        Object value = null;
        //获取参数名
        String paramName = getName(parameter);
        //获取参数类型
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType.isArray()) {
            Class<?> componentType = parameterType.getComponentType();
            value = WebTypeConverterUtils.toSimpleTypeArray(componentType, req.getParameterValues(paramName));
        } else if (List.class.isAssignableFrom(parameterType)) {
            Class<?> genericType = getGenericType(parameter);
            value = WebTypeConverterUtils.toCollection(parameterType, genericType, req.getParameterValues(paramName));
        } else {
            value = WebTypeConverterUtils.toSimpleTypeValue(parameterType, req.getParameter(paramName));
            if (value == null && ReflectionUtils.isPrimitive(parameterType)) {
                throw new IllegalArgumentException("不能把null给简单类型");
            }
            if (parameterType.isAnnotationPresent(RequestParam.class)) {
                String defaultValue = parameterType.getDeclaredAnnotation(RequestParam.class).defaultValue();
                if (!defaultValue.equals(ValueConstants.DEFAULT_NONE)) {
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    private static Class<?> getGenericType(MethodParameter parameter) {
        ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameter().getParameterizedType();
        Class<?> genericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return genericType;
    }

    private String getName(MethodParameter parameter) {
        String name = parameter.getParamName();
        Parameter param = parameter.getParameter();
        //参数上有注解，可能只是用来设置默认值，没有设置value
        if (param.isAnnotationPresent(RequestParam.class)) {
            String value = param.getDeclaredAnnotation(RequestParam.class).value();
            //没有设置value，value就会保留默认值，这个值我们不采用，
            // 仍然用方法的参数名（javassist解析出来的
            //如果你有注解，并且设置了value，我们才采用
            if (value.equals(ValueConstants.DEFAULT_NONE) == false) {
                name = value;
            }
        }
        return name;
    }
}
