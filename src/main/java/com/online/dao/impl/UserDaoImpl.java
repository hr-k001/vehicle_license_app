package com.online.dao.impl;

import com.online.dao.UserDao;
import com.online.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    // in-memory store: email -> User
    private final Map<String, User> userStore = new HashMap<>();

    @Override
    public String createUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return "Invalid user details";
        }
        if (userStore.containsKey(user.getEmail())) {
            return "User already exists";
        }
        userStore.put(user.getEmail(), user);
        return "User account created successfully";
    }

    @Override
    public String validateLogin(User user) {
        if (user == null || user.getEmail() == null) {
            return "Invalid credentials";
        }
        User stored = userStore.get(user.getEmail());
        if (stored != null && stored.getPassword().equals(user.getPassword())) {
            return "Login successful";
        }
        return "Invalid credentials";
    }
}
