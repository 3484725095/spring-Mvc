package com.nf.controller;

import com.nf.mvc.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FirstController implements HttpRequestHandler {
    @Override
    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("1111111-----------------");

        resp.getWriter().println("first ----");
    }
}
