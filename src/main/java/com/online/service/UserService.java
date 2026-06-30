package com.capgemini.service;

import com.capgemini.model.User;

public interface UserService {

    String userRegistration(User user);

    String userLogin(User user);
}
