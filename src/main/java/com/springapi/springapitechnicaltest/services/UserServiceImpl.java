package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow( ()->new NotFoundException("The user '" + username + "' could not be found" ) );
    }
}
