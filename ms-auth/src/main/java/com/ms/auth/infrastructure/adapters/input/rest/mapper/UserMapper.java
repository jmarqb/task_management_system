package com.ms.auth.infrastructure.adapters.input.rest.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PartialUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

	User toEntity(CreateUserDto createUserDto);

	User toEntity(UpdateUserDto createUserDto);

	@Mapping(target = "id", ignore = false)
	CreateUserResponseDto toResponse(User user);

	PartialUserResponseDto toPartialResponse(User user);

	@Mapping(target = "isAdmin", source = "admin")
	@Mapping(target = "isDefaultRole", source = "defaultRole")
	UserRole map(Role role);

	@Mapping(target = "data", source = "users")
	@Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
	PaginatedResponseDto toPaginatedResponse(List<?> users, int total, int page, int size, LocalDateTime timestamp);

}
