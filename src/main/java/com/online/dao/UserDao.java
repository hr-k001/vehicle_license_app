package com.capgemini.dao;

import com.capgemini.model.User;

public interface UserDao {

    String createUser(User user);

    String validateLogin(User user);
}
