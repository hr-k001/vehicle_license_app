package com.online.service;

import com.online.model.User;

public interface UserService {

    String userRegistration(User user);

    String userLogin(User user);
}
