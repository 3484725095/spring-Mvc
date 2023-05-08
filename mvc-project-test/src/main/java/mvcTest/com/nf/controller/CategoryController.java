package mvcTest.com.nf.controller;

import com.nf.mvc.arguments.RequestBody;
import com.nf.mvc.mappings.RequestMapping;
import com.nf.mvc.util.JacksonUtils;
import com.nf.mvc.view.JsonViewResult;
import mvcTest.com.nf.entity.Category;
import mvcTest.com.nf.service.CategoryService;
import mvcTest.com.nf.service.impl.CategoryServiceImpl;
import mvcTest.com.nf.vo.ResponseVO;

@RequestMapping("/category")
public class CategoryController {
    private CategoryService categoryService = new CategoryServiceImpl();

    @RequestMapping("/list")
    public JsonViewResult list() {
        return new JsonViewResult(new ResponseVO(200, "ok", categoryService.getAll()));
    }

    @RequestMapping("/insert")
    public JsonViewResult insert(@RequestBody Category category) {
        return new JsonViewResult(new ResponseVO(200, "ok", categoryService.insert(category)));
    }

    @RequestMapping("/update")
    public JsonViewResult update(@RequestBody Category category) {
        return null;
    }

    @RequestMapping("/delete")
    public JsonViewResult delete(Integer id) {
        return null;
    }
}
