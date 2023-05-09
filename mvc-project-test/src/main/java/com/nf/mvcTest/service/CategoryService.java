package com.nf.mvcTest.service;

import com.nf.mvcTest.entity.Category;

import java.util.List;

public interface CategoryService {
    int insert(String name);

    List<Category> getAll();

    int update(int id, String name);

    int delete(int id);
}
