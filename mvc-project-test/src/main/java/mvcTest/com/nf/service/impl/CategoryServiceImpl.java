package mvcTest.com.nf.service.impl;

import mvcTest.com.nf.dao.CategoryDao;
import mvcTest.com.nf.dao.Impl.CategoryDaoImpl;
import mvcTest.com.nf.entity.Category;
import mvcTest.com.nf.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public int insert(Category category) {
        return categoryDao.insert(category);
    }

    @Override
    public List<Category> getAll() {
        return categoryDao.getAll();
    }

    @Override
    public int update(Category category) {
        return categoryDao.update(category);
    }

    @Override
    public int delete(int id) {
        return categoryDao.delete(id);
    }
}
