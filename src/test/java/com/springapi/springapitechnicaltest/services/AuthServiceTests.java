package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthServiceImpl authService;

    @Test
    void testLoginFailed(){

    }
}
