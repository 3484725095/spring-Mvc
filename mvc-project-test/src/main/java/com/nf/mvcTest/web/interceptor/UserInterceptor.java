package com.nf.mvcTest.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nf.mvc.HandlerInterceptor;
import com.nf.mvc.Interceptors;
import com.nf.mvc.MvcContext;
import com.nf.mvc.util.JacksonUtils;
import com.nf.mvcTest.vo.ResponseVO;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Interceptors({"/product/insert", "/product/update", "/product/delete"})
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandler(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("userId");
        if (userId == null) {
            return false;
        }
        return true;
    }

    @Override
    public void postHandler(ServletRequest request, ServletResponse response, Object handler) throws Exception {
        HttpServletResponse resp = (HttpServletResponse) response;
        ResponseVO responseVO = new ResponseVO(500, "你没有登录过", null);
        String json = JacksonUtils.getObjectMapper().writeValueAsString(responseVO);
        resp.getWriter().print(json);
    }
}
