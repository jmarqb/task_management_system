package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.ports.input.UserUseCase;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.mapper.UserMapper;
import com.ms.auth.infrastructure.adapters.input.rest.ui.UserSignUpRestUI;

@Controller
@RequiredArgsConstructor
public class UserSignUpRestController implements UserSignUpRestUI {

	private final UserUseCase userUseCase;

	private final UserMapper userMapper;

	@Override
	public ResponseEntity<CreateUserResponseDto> register(CreateUserDto createUserDto) {
		User user = userUseCase.save(userMapper.toEntity(createUserDto));
		CreateUserResponseDto response = userMapper.toResponse(user);
		response.setRoles(user.getRoles().stream().map(userMapper::map).toList());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
