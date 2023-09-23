package com.springapi.springapitechnicaltest.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtServiceImpl jwtService;
    String jwtToken;
    String jwtTokenExpired;
    Date expirationDate;

    @Mock
    private UserDetails userDetails;

    private final String jwtSigningKey="17LRGXTF5L22VB9DY8BQ3SUFLCXRSLWQS1CUQ4FA97LA4QM7JJBVBMK79B24STCD";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", jwtSigningKey);
        Date dateToExpire = new Date(System.currentTimeMillis() + 3600_000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user");
        claims.put("exp", dateToExpire);
        expirationDate = dateToExpire;
        jwtToken = Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();

        Date expirationDate = new Date(System.currentTimeMillis() - 1000);
        jwtTokenExpired= Jwts.builder()
                .setExpiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }
    @Test
    void extractUsername() {
        String extractedUsername = jwtService.extractUsername(jwtToken);

        assertEquals("user", extractedUsername);
    }

    @Test
    void generateToken() {
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSigningKey));
        UserDetails parsedUserDetails = mapClaimsToUserDetails(
                Jwts.parserBuilder().setSigningKey(key).build()
                        .parseClaimsJws(token)
                        .getBody()
        );

        assertEquals("testUser", parsedUserDetails.getUsername());
    }

    @Test
    void isTokenExpired() {
        assertThrows(ExpiredJwtException.class,()->jwtService.isTokenExpired(jwtTokenExpired));
    }

    @Test
    void isTokenExpiredNonExpired() {
        assertFalse(jwtService.isTokenExpired(jwtToken));
    }

    @Test
    void isTokenValid() {
        String username = "user";
        when(userDetails.getUsername()).thenReturn(username);
        assertTrue(jwtService.isTokenValid(jwtToken, userDetails));
    }

    @Test
    void isTokenInvalid() {
        String username = "ddsad";
        when(userDetails.getUsername()).thenReturn(username);
        assertFalse(jwtService.isTokenValid(jwtToken, userDetails));
    }

    @Test
    void extractExpiration() {
        Date extractedDate = jwtService.extractExpiration(jwtToken);
        long toleranceMillis = 1000;
        assertTrue(Math.abs(expirationDate.getTime() - extractedDate.getTime()) <= toleranceMillis);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static UserDetails mapClaimsToUserDetails(Claims claims) {
        String username = claims.getSubject();

        return new User(username, "", new ArrayList<>());
    }
}