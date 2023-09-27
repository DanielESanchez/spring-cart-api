package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.User;

public interface UserService {

    User findUserByUsername(String username);
    public void enableUser(String username);
    public void disableUser(String username);
    User checkUser(String header, String usernameToCheck);
}
