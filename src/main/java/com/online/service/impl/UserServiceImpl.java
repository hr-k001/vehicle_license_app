package com.capgemini.service.impl;

import com.capgemini.dao.UserDao;
import com.capgemini.model.User;
import com.capgemini.service.UserService;

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
