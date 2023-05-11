package com.nf.mvcTest.service;

import com.nf.mvc.arguments.RequestBody;
import com.nf.mvcTest.entity.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    int insert(@RequestBody Product product) throws IOException;

    List<Product> getAll();

    int update(@RequestBody Product product);

    int delete(int id);

    List<Product> page(String name, int pageNo, int pageSize);
    int total(String name);
}
