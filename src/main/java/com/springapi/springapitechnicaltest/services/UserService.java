package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.User;

public interface UserService {

    User findUserByUsername(String username);
}
