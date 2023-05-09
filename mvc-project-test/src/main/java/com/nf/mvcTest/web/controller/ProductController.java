package com.nf.mvcTest.web.controller;

import com.nf.mvc.arguments.RequestParam;
import com.nf.mvcTest.entity.Product;
import com.nf.mvc.arguments.RequestBody;
import com.nf.mvc.file.MultipartFile;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.view.FiveViewResult;
import com.nf.mvc.view.JsonViewResult;
import com.nf.mvcTest.service.ProductService;
import com.nf.mvcTest.service.impl.ProductServiceImpl;
import com.nf.mvcTest.vo.ResponseVO;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.nf.mvc.handler.HandlerHelp.file;

@RequestMapping("/product")
public class ProductController {
    private ProductService service = new ProductServiceImpl();

    @RequestMapping("/list")
    public JsonViewResult list() {
        return new JsonViewResult(new ResponseVO(200, "ok", service.getAll()));
    }

    @RequestMapping("/insert")
    public JsonViewResult insert(@RequestBody Product product) {
        return new JsonViewResult(new ResponseVO(200, "ok", service.insert(product)));
    }

    @RequestMapping("/update")
    public JsonViewResult update(@RequestBody Product product) {
        return new JsonViewResult(new ResponseVO(200, "ok", service.update(product)));
    }

    @RequestMapping("/delete")
    public JsonViewResult delete(Integer id) {
        return new JsonViewResult(new ResponseVO(200, "ok", service.delete(id)));
    }

    @RequestMapping("/upload")
    public JsonViewResult upload(MultipartFile multipartFile) throws Exception {
        String uploadedFilename = multipartFile.getOriginalFilename();
        Path path = Paths.get("D:/img", uploadedFilename);
        multipartFile.transferTo(path);

        return new JsonViewResult(new ResponseVO(200, "ok", uploadedFilename));
    }

    @RequestMapping("/page")
    public JsonViewResult page(@RequestParam(value = "pageno", defaultValue = "1") int pageno,
                               @RequestParam(value = "pagesize", defaultValue = "5") int pagesize) {

        System.out.println(pageno);
        System.out.println(pagesize);
        return new JsonViewResult(new ResponseVO(200, "ok", service.page(pageno, pagesize)));
    }

    @RequestMapping("/download")
    public FiveViewResult download(String filename) {
        String realPath = "D:/img/" + filename;
        return file(realPath);
    }
}
