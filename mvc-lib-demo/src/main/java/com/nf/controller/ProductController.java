package com.nf.controller;

import com.nf.mvc.arguments.RequestParam;
import com.nf.mvc.file.MultipartFile;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.view.JsonViewResult;

import javax.servlet.http.Part;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequestMapping("/product")
public class ProductController {
    @RequestMapping("/page")
    public JsonViewResult page(@RequestParam("no") int pageNo,
                               @RequestParam(defaultValue = "5") String pageSize) {

        System.out.println("pageNo = " + pageNo);
        System.out.println("pageSize = " + pageSize);
        return new JsonViewResult(new ResponseVO(200, "ok", true));
    }

    @RequestMapping("/insert")
    public JsonViewResult insert(int id, String name) {
        System.out.println("id = " + id);
        System.out.println("name = " + name);
        return new JsonViewResult(new ResponseVO(200, "ok", true));
    }

    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile multipartFile, Part file) throws Exception {
        String uploadedFilename = multipartFile.getOriginalFilename();
        Path path = Paths.get("D:/images", uploadedFilename);
        multipartFile.transferTo(path);

        return "ok";
    }

    @RequestMapping("/upload2")
    public String upload(MultipartFile[] multipartFile) throws Exception {
        for (MultipartFile file : multipartFile) {
            String uploadedFilename = file.getOriginalFilename();
            Path path = Paths.get("D:/tmp", uploadedFilename);
            file.transferTo(path);
        }
        return "ok";
    }
}
