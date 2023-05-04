package com.nf.controller;

import com.nf.mvc.mappings.RequestMapping;

public class UserController {
    @RequestMapping("/list")
    public void list() {
        System.out.println("---------------");
    }
}
