package com.nf.controller;

import com.nf.Student;
import com.nf.mvc.ViewResult;


import javax.servlet.ServletException;
import java.io.IOException;

import static com.nf.mvc.handler.HandlerHelp.json;

public class SecondController {
    public ViewResult process() throws ServletException, IOException {
        System.out.println("222222-----------");
        Student student = new Student(1, "hello");
        return json(student);
    }
}
