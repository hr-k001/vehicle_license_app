package com.online.dao.impl;

import com.online.dao.UserDao;
import com.online.model.User;
import com.online.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    // in-memory store: email -> User
    private final Map<String, User> userStore = new HashMap<>();
    private final UserRepository userRepository;

    public UserDaoImpl() {
        this(null);
    }

    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String createUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return "Invalid user details";
        }
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setRole(normalizeRole(user.getRole()));

        if (userRepository != null) {
            if (userRepository.existsById(user.getEmail())) {
                return "User already exists";
            }
            userRepository.save(user);
            return "User account created successfully";
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
        String email = user.getEmail().trim().toLowerCase();
        String role = normalizeRole(user.getRole());
        User stored = findUser(email);
        if (stored != null && stored.getPassword().equals(user.getPassword()) && role.equals(stored.getRole())) {
            return "Login successful";
        }
        return "Invalid credentials";
    }

    private User findUser(String email) {
        if (userRepository != null) {
            Optional<User> stored = userRepository.findById(email);
            return stored.orElse(null);
        }
        return userStore.get(email);
    }

    private String normalizeRole(String role) {
        if ("rto".equalsIgnoreCase(role)) return "rto";
        return "applicant";
    }
}
