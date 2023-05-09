package com.nf.mvcTest.dao.Impl;

import com.nf.mvcTest.dao.ProductDao;
import com.nf.dbutils.SqlExecutor;
import com.nf.dbutils.SqlExecutorEx;
import com.nf.dbutils.handlers.BeanListHandler;
import com.nf.mvcTest.entity.Product;
import com.nf.mvcTest.util.DataSourceUtils;

import java.util.List;

public class ProductDaoImpl implements ProductDao {
    SqlExecutor executor = new SqlExecutorEx(DataSourceUtils.getDataSource());

    @Override
    public int insert(Product product) {
        String sql = "insert into product(name,image,price,status,quantity,cid) values(?,?,?,?,?,?)";
        return executor.update(sql, product.getName(), product.getImage(), product.getPrice(), product.getStatus(), product.getQuantity(), product.getCid());
    }

    @Override
    public List<Product> getAll() {
        String sql = "select p.id, p.name, p.image, p.price, p.status, p.quantity,c.cid from product p left join category c on p.cid = c.cid;";
        BeanListHandler<Product> handler = new BeanListHandler<>(Product.class);
        return executor.query(sql, handler);
    }

    @Override
    public int update(Product product) {
        String sql = "update product set name=? where id=?";
        return executor.update(sql, product.getName(), product.getId());
    }

    @Override
    public int delete(int id) {
        String sql = "delete from product where id=?";
        return executor.update(sql, id);
    }

    @Override
    public List<Product> page(int pageNo, int pageSize) {
        String sql = "select p.id, p.name, p.image, p.price, p.status, p.quantity,c.cid from product p left join category c on p.cid = c.cid limit ?,?";
        BeanListHandler<Product> handler = new BeanListHandler<>(Product.class);
        return executor.query(sql, handler, pageNo, pageSize);
    }
}
