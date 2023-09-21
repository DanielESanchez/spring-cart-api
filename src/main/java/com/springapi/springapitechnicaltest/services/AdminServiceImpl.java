package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserService userService;
    @Override
    public void disableUser(String username) {
        User userFound = userService.findUserByUsername(username);
        userFound.setEnabled(false);
        userRepository.save(userFound);
    }

    @Override
    public void enableUser(String username) {
        User userFound = userService.findUserByUsername(username);
        userFound.setEnabled(true);
        userRepository.save(userFound);
    }
}
