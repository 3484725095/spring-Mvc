package com.nf.mvcTest.web.controller;

import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.view.JsonViewResult;
import com.nf.mvcTest.service.CategoryService;
import com.nf.mvcTest.service.impl.CategoryServiceImpl;
import com.nf.mvcTest.vo.ResponseVO;

@RequestMapping("/category")
public class CategoryController {
    private CategoryService categoryService = new CategoryServiceImpl();

    @RequestMapping("/list")
    public JsonViewResult list() {
        return new JsonViewResult(new ResponseVO(200, "ok", categoryService.getAll()));
    }

    @RequestMapping("/insert")
    public JsonViewResult insert(String name) {
        return new JsonViewResult(new ResponseVO(200, "ok", categoryService.insert(name)));
    }

    @RequestMapping("/update")
    public JsonViewResult update(int id, String name) {
        return new JsonViewResult(new ResponseVO(200, "ok", categoryService.update(id, name)));
    }

    @RequestMapping("/delete")
    public JsonViewResult delete(Integer id) {
        return new JsonViewResult(new ResponseVO(200, "ok", categoryService.delete(id)));
    }
}
