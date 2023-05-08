package mvcTest.com.nf.dao.Impl;

import com.nf.dbutils.ResultSetHandler;
import com.nf.dbutils.SqlExecutor;
import com.nf.dbutils.SqlExecutorEx;
import com.nf.dbutils.handlers.ArrayListHandler;
import com.nf.dbutils.handlers.BeanListHandler;
import mvcTest.com.nf.dao.CategoryDao;
import mvcTest.com.nf.entity.Category;
import mvcTest.com.nf.util.DataSourceUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    SqlExecutor sqlExecutor = new SqlExecutorEx(DataSourceUtils.getDataSource());

    @Override
    public int insert(Category category) {
        String sql = "insert into category(name) values(?)";
        return sqlExecutor.update(sql, category.getName());
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
    public int update(Category category) {
        String sql = "update category set name=? where cid=?";
        return sqlExecutor.update(sql, category.getName(), category.getCid());
    }

    @Override
    public int delete(int id) {
        String sql = "delete from category where cid=?";
        return sqlExecutor.update(sql, id);
    }
}
