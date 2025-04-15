package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.ports.input.UserUseCase;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.mapper.UserMapper;
import com.ms.auth.infrastructure.adapters.input.rest.ui.UserRestUI;

@Controller
@RequiredArgsConstructor
public class UserRestController implements UserRestUI {

	private final UserUseCase userUseCase;

	private final UserMapper userMapper;

	@Override
	public ResponseEntity<PaginatedResponseDto> search(SearchBodyDto searchBodyDto) {
		List<User> usersList = userUseCase.search(searchBodyDto.getSearch(), searchBodyDto.getPage(),
			searchBodyDto.getSize(), searchBodyDto.getSort());

		List<CreateUserResponseDto> response = new ArrayList<>();
		usersList.forEach(user -> response.add(userMapper.toResponse(user)));

		return ResponseEntity.status(HttpStatus.OK).body(userMapper.toPaginatedResponse(response, usersList.size(),
			searchBodyDto.getPage(), searchBodyDto.getSize(), LocalDateTime.now()));
	}

	@Override
	public ResponseEntity<CreateUserResponseDto> findUser(Long id) {
		User user = userUseCase.findUser(id);
		return ResponseEntity.status(HttpStatus.OK).body(userMapper.toResponse(user));
	}

	@Override
	public ResponseEntity<CreateUserResponseDto> updateUser(Long id, UpdateUserDto updateUserDto) {
		updateUserDto.setId(id);
		User user = userUseCase.updateUser(userMapper.toEntity(updateUserDto));
		return ResponseEntity.status(HttpStatus.OK).body(userMapper.toResponse(user));
	}

	@Override
	public ResponseEntity<DeleteResponseDto> removeUser(Long id) {
		userUseCase.deleteUser(id);
		return new ResponseEntity<>(new DeleteResponseDto(true, 1), HttpStatus.OK);
	}
}
