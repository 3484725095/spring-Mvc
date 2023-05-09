package com.nf.mvcTest.dao;

import com.nf.mvcTest.entity.Product;
import com.nf.mvc.arguments.RequestBody;

import java.util.List;

public interface ProductDao {
    int insert(@RequestBody Product product);

    List<Product> getAll();

    int update(@RequestBody Product product);

    int delete(int id);

    List<Product> page(int pageNo, int pageSize);
}
