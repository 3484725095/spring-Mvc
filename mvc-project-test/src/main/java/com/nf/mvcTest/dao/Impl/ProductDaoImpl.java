package com.nf.mvcTest.dao.Impl;

import com.nf.dbutils.handlers.ScalarHandler;
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
        String sql = "select p.id, p.name, p.image, p.price, p.status, p.quantity,c.cid,c.name as typeName from product p left join category c on p.cid = c.cid;";
        BeanListHandler<Product> handler = new BeanListHandler<>(Product.class);
        return executor.query(sql, handler);
    }

    @Override
    public int update(Product product) {
        String sql = "update product set name=?,image=?,price=?,status=?,quantity=?,cid=? where id=?";
        return executor.update(sql, product.getName(), product.getImage(), product.getPrice(), product.getStatus(), product.getQuantity(), product.getCid(), product.getId());
    }

    @Override
    public int delete(int id) {
        String sql = "delete from product where id=?";
        return executor.update(sql, id);
    }

    @Override
    public List<Product> page(String name, int pageNo, int pageSize) {
        String sql = "select p.id, p.name, p.image, p.price, p.status, p.quantity,c.cid,c.name as typeName from product p left join category c on p.cid = c.cid where c.name=? limit ?,?";
        BeanListHandler<Product> handler = new BeanListHandler<>(Product.class);
        return executor.query(sql, handler, name, pageNo, pageSize);
    }

    @Override
    public int total(String name) {
        String sql = "select COUNT(*)\n" + "from product p\n" + "         left join category c on c.cid = p.cid\n" + "where c.name=?";
        return Integer.parseInt(executor.query(sql, new ScalarHandler<>(), name).toString());
    }
}
