package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.ports.input.UserUseCase;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.RoleToUsersDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.mapper.UserMapper;
import com.ms.auth.infrastructure.adapters.input.rest.ui.RoleUserRestUI;

@Controller
@RequiredArgsConstructor
public class RoleUserRestController implements RoleUserRestUI {

	private final UserUseCase userUseCase;

	private final UserMapper userMapper;

	@Override
	public ResponseEntity<PaginatedResponseDto> addRoleToManyUsers(RoleToUsersDto roleToUsersDto) {
		List<User> userList = userUseCase.addRoleToManyUsers(roleToUsersDto.getUsersId(), roleToUsersDto.getRoleId());
		List<CreateUserResponseDto> response = userList.stream().map(userMapper::toResponse).toList();
		return ResponseEntity.status(HttpStatus.OK).body(userMapper.toPaginatedResponse(response, userList.size(),
			0, userList.size(), LocalDateTime.now()));
	}

	@Override
	public ResponseEntity<PaginatedResponseDto> removeRoleToManyUsers(RoleToUsersDto roleToUsersDto) {
		List<User> userList = userUseCase.removeRoleToManyUsers(roleToUsersDto.getUsersId(), roleToUsersDto.getRoleId());

		List<CreateUserResponseDto> response = userList.stream().map(userMapper::toResponse).toList();

		return ResponseEntity.status(HttpStatus.OK).body(userMapper.toPaginatedResponse(response, userList.size(),
			0, userList.size(), LocalDateTime.now()));
	}
}
