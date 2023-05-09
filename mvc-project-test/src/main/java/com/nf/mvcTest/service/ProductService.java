package com.nf.mvcTest.service;

import com.nf.mvc.arguments.RequestBody;
import com.nf.mvcTest.entity.Product;

import java.util.List;

public interface ProductService {
    int insert(@RequestBody Product product);

    List<Product> getAll();

    int update(@RequestBody Product product);

    int delete(int id);

    List<Product> page(int pageNo, int pageSize);
}
