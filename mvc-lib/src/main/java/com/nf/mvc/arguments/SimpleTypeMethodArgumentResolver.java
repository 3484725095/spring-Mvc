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

public class SimpleTypeMethodArgumentResolver extends AbstractCommonTypeMethodArgumentResolver {

    protected boolean supportsInternal(Class<?> type) {
        return ReflectionUtils.isSimpleType(type);
    }

    @Override
    protected Object resolveArgumentInternal(Class<?> type, Object parameterValue, MethodParameter methodParameter) throws Exception {
        Object value = parameterValue;
        if (value == null) {
            if (methodParameter.getParameter().isAnnotationPresent(RequestParam.class)) {
                String defaultValue = methodParameter.getParameter().getDeclaredAnnotation(RequestParam.class).defaultValue();
                //用户可能只是利用RequestParam设置了参数名字，没有设置默认值
                if (!defaultValue.equals(ValueConstants.DEFAULT_NONE)) {
                    value = defaultValue;
                }
            }
        }
        if (value == null && ReflectionUtils.isPrimitive(type)) {
            throw new IllegalArgumentException("参数名:" + methodParameter.getParamName() + " 的值为null，不能把null给简单类型:" + type);
        }
        if (value != null) {
            value = WebTypeConverterUtils.toSimpleTypeValue(type, value.toString());
        }

        return value;
    }

    @Override
    protected Object[] getSource(MethodParameter methodParameter, HttpServletRequest request) {
        return request.getParameterValues(methodParameter.getParamName());
    }
}
