package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.ConflictException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Role;
import com.springapi.springapitechnicaltest.models.RoleName;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.models.UserRole;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    private User userEnabled;
    private User userDisabled;
    private User adminEnabled;

    @BeforeEach
    public void setup() {
        UserRole adminRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_ADMIN).build()).build();
        UserRole userRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build();

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole);

        Set<UserRole> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);

        userEnabled = new User();
        userEnabled.setUsername("user");
        userEnabled.setEnabled(true);
        userEnabled.setUserRoles(userRoles);

        userDisabled = new User();
        userDisabled.setUsername("disabled");
        userDisabled.setEnabled(true);
        userDisabled.setUserRoles(userRoles);

        adminEnabled = new User();
        adminEnabled.setUsername("user");
        adminEnabled.setEnabled(true);
        adminEnabled.setUserRoles(adminRoles);
    }

    @Test
    public void testFindUserByUsername() {
        when(userRepository.findUserByUsername(userEnabled.getUsername())).thenReturn(Optional.of(userEnabled));
        User foundUser = userService.findUserByUsername(userEnabled.getUsername());
        assertNotNull(foundUser);
        assertSame(foundUser.getUsername(), userEnabled.getUsername());
        Assertions.assertTrue(foundUser.isEnabled());
    }

    @Test
    public void testFindUserByUsernameNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findUserByUsername("userNoSaved"));
    }

    @Test
    public void disableUser() {
        when(userRepository.findUserByUsername(userEnabled.getUsername())).thenReturn(Optional.of(userEnabled));

        userService.disableUser(userEnabled.getUsername());
        Assertions.assertFalse(userEnabled.isEnabled());
        verify(userRepository, times(1)).save(userEnabled);
    }

    @Test
    public void disableAdminUser() {
        when(userRepository.findUserByUsername(adminEnabled.getUsername())).thenReturn(Optional.of(adminEnabled));

        assertThrows(ConflictException.class, () -> userService.disableUser(adminEnabled.getUsername()));
    }

    @Test
    void enableUser() {
        when(userRepository.findUserByUsername(userDisabled.getUsername())).thenReturn(Optional.of(userDisabled));

        userService.enableUser(userDisabled.getUsername());
        Assertions.assertTrue(userDisabled.isEnabled());
        verify(userRepository, times(1)).save(userDisabled);
    }
}