package com.nf.mvc.arguments;

import com.nf.mvc.HandlerContext;
import com.nf.mvc.MethodArgumentResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ServletApiMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supports(MethodParameter parameter) {
        Class<?> paramType = parameter.getParamType();
        return HttpServletRequest.class.isAssignableFrom(paramType) || HttpServletResponse.class.isAssignableFrom(paramType) || HttpSession.class.isAssignableFrom(paramType) || ServletContext.class.isAssignableFrom(paramType);
    }

    /**
     * 参数解析，是从请求中获取数据的，所以方法设计没有response对象是合理的
     * 但这样给我们带来一个解析的问题，无法获取到response对象
     * request与response对象必须来自于DispatcherServlet的service
     *
     * @param parameter
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest request) throws Exception {
        Class<?> paramType = parameter.getParamType();
        HandlerContext context = HandlerContext.getContext();
        if (HttpServletRequest.class.isAssignableFrom(paramType)) {
            return request;
        }
        if (HttpServletResponse.class.isAssignableFrom(paramType)) {
            return context.getResponse();
        }
        if (HttpSession.class.isAssignableFrom(paramType)) {
            // return request.getSession();//这样可以
            return context.getSession();
        }
        if (ServletContext.class.isAssignableFrom(paramType)) {
            return request.getServletContext();
        }
        return null;
    }
}
