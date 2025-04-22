package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.LoginDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.AuthResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.ui.AuthRestUI;
import com.ms.auth.infrastructure.security.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthRestController implements AuthRestUI {

	private final AuthService authService;

	@Override
	public ResponseEntity<AuthResponseDto> login(LoginDto loginRequest) {
		String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
		return ResponseEntity.status(HttpStatus.OK).body(new AuthResponseDto(token));
	}
}
