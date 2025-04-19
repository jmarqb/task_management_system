package com.ms.auth.application.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.application.ports.input.AuthUseCase;
import com.ms.auth.application.ports.input.JwtUseCase;
import com.ms.auth.domain.model.CustomUserDetails;

@Component
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

	private final JwtUseCase jwtUseCase;

	private final AuthenticationManager authenticationManager;

	@Override
	public String login(String email, String password) {
		{
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
			try {
				Authentication authentication = authenticationManager.authenticate(
					token
				);
				CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

				return jwtUseCase.generateToken(user);

			} catch (BadCredentialsException e) {
				throw new BadCredentialsException("Invalid email or password", e);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error generating token", e);
			}
		}
	}
}
