package com.springapi.springapitechnicaltest.services;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;

public interface JwtService {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails);
    boolean isTokenExpired(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    Date extractExpiration(String token);
}