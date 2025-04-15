package com.ms.auth.application.service.impl;

import com.ms.auth.application.impl.JwtUseCaseImpl;
import com.ms.auth.domain.model.CustomUserDetails;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JwtUseCaseImplTest {

    private JwtUseCaseImpl jwtUseCase;
    private SecretKey returnedSecret;

    private CustomUserDetails mockUser;

    @BeforeEach
    void setUp() {
        String TEST_SECRET = "mi_testing_secret_value_key_123_456_789";
        returnedSecret = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        jwtUseCase = new JwtUseCaseImpl(TEST_SECRET);

        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        mockUser = new CustomUserDetails(
                1L,
                "testUser",
                "password",
                true,
                true,
                true,
                true,
                roles);
    }

    @Test
    void generateToken_ShouldReturnValidToken() throws JsonProcessingException {

        String token = jwtUseCase.generateToken(mockUser);
        assertThat(token).isNotNull();
        assertThat(token.isEmpty()).isFalse();

        Claims claims = Jwts.parser()
                .verifyWith(returnedSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("testUser", claims.get("username"));
        assertThat(claims.get("authorities")).isNotNull();
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() throws JsonProcessingException {
        CustomUserDetails user = mockUser;

        String token = jwtUseCase.generateToken(user);
        String extractedUsername = jwtUseCase.extractUsername(token);

        assertEquals("testUser", extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() throws JsonProcessingException {
        CustomUserDetails userDetails = mockUser;

        String token = jwtUseCase.generateToken(userDetails);

        assertThat(jwtUseCase.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidUsername() throws JsonProcessingException {
        CustomUserDetails otherUser = new CustomUserDetails(
            2L,
            "otherUser",
            "password",
            true,
            true,
            true,
            true,
            Collections.emptyList()
        );

        String token = jwtUseCase.generateToken(mockUser);

        assertThat(jwtUseCase.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForNewToken() throws JsonProcessingException {

        String token = jwtUseCase.generateToken(mockUser);

        assertThat(jwtUseCase.isTokenExpired(token)).isFalse();
    }

    @Test
    void isTokenExpired_ShouldReturnTrueForExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("testUser")
                .expiration(new Date(System.currentTimeMillis() - 3800000)) // Expired token
                .issuedAt(new Date(System.currentTimeMillis() - 7200000))
                .signWith(returnedSecret)
                .compact();

        assertThat(jwtUseCase.isTokenExpired(expiredToken)).isTrue();
    }
}
