package com.nf.mvcTest.service;

import com.nf.mvcTest.entity.User;

public interface UserService {
    User login(int userid, int password);

    int selectById(int userid);
}
