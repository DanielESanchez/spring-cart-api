package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.User;

import java.util.List;

public interface UserService {

    User findUserByUsername(String username);
    void enableUser(String username);
    void disableUser(String username);
    User checkUser(String header, String usernameToCheck);
    List<User> getAllUsers();
}
