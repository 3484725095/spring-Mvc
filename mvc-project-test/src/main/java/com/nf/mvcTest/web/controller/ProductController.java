package com.nf.mvcTest.web.controller;

import com.nf.mvc.arguments.RequestParam;
import com.nf.mvcTest.entity.Category;
import com.nf.mvcTest.entity.PageBean;
import com.nf.mvcTest.entity.Product;
import com.nf.mvc.arguments.RequestBody;
import com.nf.mvc.file.MultipartFile;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.view.FiveViewResult;
import com.nf.mvc.view.JsonViewResult;
import com.nf.mvcTest.service.CategoryService;
import com.nf.mvcTest.service.ProductService;
import com.nf.mvcTest.service.impl.CategoryServiceImpl;
import com.nf.mvcTest.service.impl.ProductServiceImpl;
import com.nf.mvcTest.vo.ResponseVO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.nf.mvc.handler.HandlerHelp.file;
import static com.nf.mvc.handler.HandlerHelp.json;

@RequestMapping("/product")
public class ProductController {
    private ProductService productService = new ProductServiceImpl();

    @RequestMapping("/list")
    public JsonViewResult list() {
        return new JsonViewResult(new ResponseVO(200, "ok", productService.getAll()));
    }

    @RequestMapping("/insert")
    public JsonViewResult insert(MultipartFile multipartFile, Product product) throws IOException {
        product.setMultipartFile(multipartFile);
        return json(new ResponseVO(200, "ok", productService.insert(product)));
    }

    @RequestMapping("/update")
    public JsonViewResult update(@RequestBody Product product) {
        System.out.println("product = " + product);
        return new JsonViewResult(new ResponseVO(200, "ok", productService.update(product)));
    }

    @RequestMapping("/delete")
    public JsonViewResult delete(Integer id) {
        return new JsonViewResult(new ResponseVO(200, "ok", productService.delete(id)));
    }

    @RequestMapping("/page")
    public JsonViewResult page(@RequestParam("cname") String name, @RequestParam(defaultValue = "1") int pageno, @RequestParam(defaultValue = "5") int pagesize) {
        PageBean pageBean = new PageBean();
        pageBean.setTotal(productService.total(name));
        pageBean.setRecords(productService.page(name, pageno, pagesize));
        return new JsonViewResult(new ResponseVO(200, "ok", pageBean));
    }
}
