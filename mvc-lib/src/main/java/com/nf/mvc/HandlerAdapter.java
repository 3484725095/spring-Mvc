package com.nf.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
    boolean supports(Object handler);

    ViewResult handler(HttpServletResponse resp, HttpServletRequest req, Object handler) throws Exception;
}
