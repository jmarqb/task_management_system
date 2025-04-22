package com.ms.auth.infrastructure.security.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.infrastructure.security.model.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;

	public String login(String email, String password) {
		{
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
			try {
				Authentication authentication = authenticationManager.authenticate(
					token
				);
				CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

				return jwtService.generateToken(user);

			} catch (BadCredentialsException e) {
				throw new BadCredentialsException("Invalid email or password", e);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error generating token", e);
			}
		}
	}
}
