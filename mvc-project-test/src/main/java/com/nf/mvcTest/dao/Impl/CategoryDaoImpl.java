package com.nf.mvcTest.dao.Impl;

import com.nf.dbutils.SqlExecutor;
import com.nf.dbutils.SqlExecutorEx;
import com.nf.dbutils.handlers.BeanListHandler;
import com.nf.mvcTest.entity.Category;
import com.nf.mvcTest.util.DataSourceUtils;
import com.nf.mvcTest.dao.CategoryDao;

import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    SqlExecutor sqlExecutor = new SqlExecutorEx(DataSourceUtils.getDataSource());

    @Override
    public int insert(String name) {
        String sql = "insert into category(name) values(?)";
        return sqlExecutor.update(sql, name);
    }

    @Override
    public List<Category> getAll() {
        List<Category> result = null;
        String sql = "select cid,name from category;";
        BeanListHandler<Category> handler = new BeanListHandler<>(Category.class);
        result = sqlExecutor.query(sql, handler);

        return result;
    }

    @Override
    public int update(int id, String name) {
        String sql = "update category set name=? where cid=?";
        return sqlExecutor.update(sql, name, id);
    }

    @Override
    public int delete(int id) {
        String sql = "delete from category where cid=?";
        return sqlExecutor.update(sql, id);
    }
}
