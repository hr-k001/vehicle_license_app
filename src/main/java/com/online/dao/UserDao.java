package com.online.dao;

import com.online.model.User;

public interface UserDao {

    String createUser(User user);

    String validateLogin(User user);
}
