package com.nf.mvcTest.web.controller;

import com.nf.mvc.ViewResult;
import com.nf.mvc.arguments.RequestBody;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.view.JsonViewResult;
import com.nf.mvcTest.entity.Product;
import com.nf.mvcTest.service.UserService;
import com.nf.mvcTest.service.impl.UserServiceImpl;
import com.nf.mvcTest.vo.ResponseVO;

import static com.nf.mvc.handler.HandlerHelp.json;

@RequestMapping("/user")
public class UserController {
    UserService service = new UserServiceImpl();

    @RequestMapping("/login")
    public JsonViewResult login(int userid, int password) {
        return json(new ResponseVO(200, "ok", service.login(userid, password)));
    }

    @RequestMapping("/logout")
    public ViewResult logout() {
        return json(new ResponseVO(200, "ok", null));
    }
}
