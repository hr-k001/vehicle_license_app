package com.online.service.impl;

import com.online.dao.UserDao;
import com.online.model.User;
import com.online.service.UserService;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String userRegistration(User user) {
        return userDao.createUser(user);
    }

    @Override
    public String userLogin(User user) {
        return userDao.validateLogin(user);
    }
}
