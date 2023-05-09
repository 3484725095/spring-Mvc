package com.nf.mvcTest.service.impl;

import com.nf.mvcTest.entity.Product;
import com.nf.mvcTest.service.ProductService;
import com.nf.mvcTest.dao.Impl.ProductDaoImpl;
import com.nf.mvcTest.dao.ProductDao;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public int insert(Product product) {
        return productDao.insert(product);
    }

    @Override
    public List<Product> getAll() {
        return productDao.getAll();
    }

    @Override
    public int update(Product product) {
        return productDao.update(product);
    }

    @Override
    public int delete(int id) {
        return productDao.delete(id);
    }

    @Override
    public List<Product> page(int pageNo, int pageSize) {
        return productDao.page(pageNo, pageSize);
    }
}
