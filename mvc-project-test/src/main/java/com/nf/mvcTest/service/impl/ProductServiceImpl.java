package com.nf.mvcTest.service.impl;

import com.nf.mvc.file.MultipartFile;
import com.nf.mvc.file.StandardMultipartFile;
import com.nf.mvcTest.entity.Product;
import com.nf.mvcTest.service.ProductService;
import com.nf.mvcTest.dao.Impl.ProductDaoImpl;
import com.nf.mvcTest.dao.ProductDao;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public int insert(Product product) throws IOException {
        MultipartFile multipartFile = product.getMultipartFile();
        String originalFilename = multipartFile.getOriginalFilename();
        product.setImage(originalFilename);
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
    public List<Product> page(String name, int pageNo, int pageSize) {
        pageNo = (pageNo - 1) * pageSize;
        return productDao.page(name, pageNo, pageSize);
    }

    @Override
    public int total(String name) {
        return productDao.total(name);
    }
}
