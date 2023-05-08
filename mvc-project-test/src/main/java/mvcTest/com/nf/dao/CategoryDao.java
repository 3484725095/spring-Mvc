package mvcTest.com.nf.dao;

import mvcTest.com.nf.entity.Category;

import java.util.List;

public interface CategoryDao {

    int insert(Category category);

    List<Category> getAll();

    int update(Category category);

    int delete(int id);
}
