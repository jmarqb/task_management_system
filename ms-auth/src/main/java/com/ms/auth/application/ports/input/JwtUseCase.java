package com.ms.auth.application.ports.input;

import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.domain.model.CustomUserDetails;

public interface JwtUseCase {

	String generateToken(CustomUserDetails user) throws JsonProcessingException;

	String extractUsername(String token);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isTokenExpired(String token);
}
