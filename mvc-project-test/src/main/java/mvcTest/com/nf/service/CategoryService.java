package mvcTest.com.nf.service;

import mvcTest.com.nf.entity.Category;

import java.util.List;

public interface CategoryService {
    int insert(Category category);

    List<Category> getAll();

    int update(Category category);

    int delete(int id);
}
