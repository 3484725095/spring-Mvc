package com.nf.mvcTest.service.impl;

import com.nf.mvcTest.dao.Impl.UserDaoImpl;
import com.nf.mvcTest.dao.UserDao;
import com.nf.mvcTest.entity.User;
import com.nf.mvcTest.service.UserService;

public class UserServiceImpl implements UserService {
    private UserDao dao = new UserDaoImpl();

    @Override
    public User login(int userid, int password) {
        return dao.login(userid, password);
    }

    @Override
    public int selectById(int userid) {
        return 0;
    }
}
