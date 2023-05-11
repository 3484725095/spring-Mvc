package com.nf.mvcTest.dao;

import com.nf.mvcTest.entity.User;

public interface UserDao {
    User login(int userid, int password);

    int selectById(int userid);
}
