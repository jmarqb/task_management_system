package com.ms.auth.application.ports.input;

import com.ms.auth.domain.model.CustomUserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtUseCase {

	String generateToken(CustomUserDetails user) throws JsonProcessingException;

	String extractUsername(String token);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isTokenExpired(String token);
}
