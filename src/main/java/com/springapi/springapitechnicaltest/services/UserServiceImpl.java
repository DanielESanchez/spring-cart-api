package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.ConflictException;
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
        User userFound = userRepository.findUserByUsername(username)
                .orElseThrow( () -> new NotFoundException("The user '" + username + "' could not be found" ) );
        if(!userFound.isEnabled()) throw  new NotFoundException("The user '" + username + "' could not be found" );
        return userFound;
    }

    @Override
    public void disableUser(String username) {
        User userFound = findUserByUsername(username);
        if( userFound.hasRole("ADMIN") ) throw new ConflictException("This user cannot be changed");
        userFound.setEnabled(false);
        userRepository.save(userFound);
    }

    @Override
    public void enableUser(String username) {
        User userFound = findUserByUsername(username);
        if( userFound.hasRole("ADMIN") ) throw new ConflictException("This user cannot be changed");
        userFound.setEnabled(true);
        userRepository.save(userFound);
    }
}
