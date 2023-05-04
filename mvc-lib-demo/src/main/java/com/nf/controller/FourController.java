package com.nf.controller;

import com.nf.Student;
import com.nf.mvc.HandlerContext;
import com.nf.mvc.arguments.RequestBody;
import com.nf.mvc.file.MultipartFile;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.view.FiveViewResult;
import com.nf.mvc.view.JsonViewResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.nf.mvc.handler.HandlerHelp.file;

@RequestMapping("/four")
public class FourController {

    @RequestMapping("/list")
    public void m1(HttpServletRequest req) {
        System.out.println("req.hashCode() = " + req.hashCode());
        System.out.println("HandlerContext.getContext().getRequest().hashCode() = " + HandlerContext.getContext().getRequest().hashCode());
        System.out.println("req.getMethod() = " + req.getMethod());
        System.out.println("m1 in proudct------");
    }

    @RequestMapping(("/download"))
    public FiveViewResult download(String fileName) {
        String realPath = "D:/images/" + fileName;
        return file(realPath);
    }

    @RequestMapping("/upload2")
    public String upload(MultipartFile[] multipartFile, List<Part> parts) throws Exception {
        for (MultipartFile file : multipartFile) {
            String uploadedFilename = file.getOriginalFilename();
            Path path = Paths.get("D:/images/", uploadedFilename);
            file.transferTo(path);
        }
        return "ok";
    }

    @RequestMapping("/json")
    public JsonViewResult json(@RequestBody Student student) {
        System.out.println("student = " + student);
        return new JsonViewResult(new ResponseVO(200, "ok", true));
    }

    @RequestMapping("/delete")
    public JsonViewResult delete(Integer[] ids, List<Integer> id2s, Student student) {
        System.out.println("====delete in product-");
        System.out.println("ids.length = " + ids.length);
        System.out.println("ids = " + Arrays.toString(ids));
        System.out.println("id2s.size() = " + id2s.size());
        System.out.println("student = " + student);
        id2s.forEach(System.out::println);
        return new JsonViewResult(new ResponseVO(200, "ok", true));
    }
}
