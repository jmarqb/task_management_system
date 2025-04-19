package com.ms.auth.infrastructure.adapters.input.rest.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.ms.auth.domain.model.Role;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateRoleResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	Role toEntity(CreateRoleDto createRoleDto);

	Role toEntity(UpdateRoleDto createRoleDto);

	@Mapping(target = "id", ignore = false)
	@Mapping(target = "isAdmin", source = "admin")
	@Mapping(target = "isDefaultRole", source = "defaultRole")
	CreateRoleResponseDto toResponse(Role role);

	@Mapping(target = "data", source = "roles")
	@Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
	PaginatedResponseDto toPaginatedResponse(List<?> roles, int total, int page, int size, LocalDateTime timestamp);

}
