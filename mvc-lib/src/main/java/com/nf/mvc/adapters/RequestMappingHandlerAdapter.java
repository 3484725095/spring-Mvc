package com.nf.mvc.adapters;

import com.nf.mvc.HandlerAdapter;
import com.nf.mvc.MvcContext;
import com.nf.mvc.ViewResult;
import com.nf.mvc.arguments.HandlerMethodArgumentResolverComposite;
import com.nf.mvc.arguments.MethodParameter;
import com.nf.mvc.handler.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static com.nf.mvc.ViewResult.handleViewResult;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();

    public RequestMappingHandlerAdapter() {
        resolvers.addResolvers(MvcContext.getMvcContext().getArgumentResolvers());
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod && ((HandlerMethod) handler).getHandlerMethod() != null;
    }

    @Override
    public ViewResult handler(HttpServletResponse resp, HttpServletRequest req, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object instance = handlerMethod.getInstance();
        Object[] paramValues = resolveParamValues(req, handlerMethod);
        Method method = handlerMethod.getHandlerMethod();

        Object handlerResult = method.invoke(instance, paramValues);
        return handleViewResult(handlerResult);
    }

    private Object resolveArgument(MethodParameter parameter, HttpServletRequest req) throws Exception {
        if (resolvers.supports(parameter)) {
            return resolvers.resolveArgument(parameter,req);
        }
        return null;
    }

    private Object[] resolveParamValues(HttpServletRequest req, HandlerMethod handlerMethod) throws Exception {
        int parameterCount = handlerMethod.getParameterCount();
        Object[] paramValues = new Object[parameterCount];

        for (int i = 0; i < parameterCount; i++) {
            MethodParameter parameter = handlerMethod.getMethodParameters()[i];
            paramValues[i] = resolveArgument(parameter, req);
        }
        return paramValues;
    }
}
