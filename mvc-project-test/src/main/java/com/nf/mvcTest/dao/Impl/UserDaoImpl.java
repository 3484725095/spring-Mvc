package com.nf.mvcTest.dao.Impl;

import com.nf.dbutils.SqlExecutor;
import com.nf.dbutils.SqlExecutorEx;
import com.nf.dbutils.handlers.BeanHandler;
import com.nf.mvcTest.dao.UserDao;
import com.nf.mvcTest.entity.User;
import com.nf.mvcTest.util.DataSourceUtils;

public class UserDaoImpl implements UserDao {
    SqlExecutor executor = new SqlExecutorEx(DataSourceUtils.getDataSource());

    @Override
    public User login(int userid, int password) {
        String sql = "select userid,userpassword as password from user where userid=? and userpassword=?";
        return executor.query(sql, new BeanHandler<>(User.class), userid, password);
    }

    @Override
    public int selectById(int userid) {
        return 0;
    }
}
