package com.online;

import com.online.dao.UserDao;
import com.online.dao.impl.UserDaoImpl;
import com.online.model.User;
import com.online.service.UserService;
import com.online.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserDao userDao = new UserDaoImpl();
        userService = new UserServiceImpl(userDao);
    }

    // US-001: Applicant registers in the portal -> User account created successfully
    @Test
    void testUserRegistration_Success() {
        User user = new User("applicant1@example.com", "Pass@123");
        String result = userService.userRegistration(user);
        assertEquals("User account created successfully", result);
    }

    @Test
    void testUserRegistration_DuplicateEmail() {
        User user = new User("applicant2@example.com", "Pass@123");
        userService.userRegistration(user);
        String result = userService.userRegistration(user);
        assertEquals("User already exists", result);
    }

    // US-002: Applicant logs into portal -> Valid credentials allow access
    @Test
    void testUserLogin_ValidCredentials() {
        User user = new User("applicant3@example.com", "Pass@123");
        userService.userRegistration(user);

        String result = userService.userLogin(new User("applicant3@example.com", "Pass@123"));
        assertEquals("Login successful", result);
    }

    @Test
    void testUserLogin_InvalidCredentials() {
        User user = new User("applicant4@example.com", "Pass@123");
        userService.userRegistration(user);

        String result = userService.userLogin(new User("applicant4@example.com", "WrongPass"));
        assertEquals("Invalid credentials", result);
    }
}
