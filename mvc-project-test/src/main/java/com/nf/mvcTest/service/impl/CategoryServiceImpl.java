package com.nf.mvcTest.service.impl;

import com.nf.mvcTest.entity.Category;
import com.nf.mvcTest.dao.CategoryDao;
import com.nf.mvcTest.dao.Impl.CategoryDaoImpl;
import com.nf.mvcTest.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public int insert(String name) {
        return categoryDao.insert(name);
    }

    @Override
    public List<Category> getAll() {
        return categoryDao.getAll();
    }

    @Override
    public int update(int id, String name) {
        return categoryDao.update(id, name);
    }

    @Override
    public int delete(int id) {
        return categoryDao.delete(id);
    }
}
