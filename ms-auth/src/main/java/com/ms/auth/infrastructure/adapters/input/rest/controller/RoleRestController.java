package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.ports.input.RoleUseCase;
import com.ms.auth.domain.model.Role;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateRoleResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.mapper.RoleMapper;
import com.ms.auth.infrastructure.adapters.input.rest.ui.RoleRestUI;

@Controller
@RequiredArgsConstructor
public class RoleRestController  implements RoleRestUI {

	private final RoleUseCase roleUseCase;

	private final RoleMapper roleMapper;

	@Override
	public ResponseEntity<CreateRoleResponseDto> create(CreateRoleDto createRoleDto) {
		Role role = roleUseCase.save(roleMapper.toEntity(createRoleDto));
		return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toResponse(role));
	}

	@Override
	public ResponseEntity<PaginatedResponseDto> search(SearchBodyDto searchBodyDto) {
		List<Role> roleList = roleUseCase.search(searchBodyDto.getSearch(), searchBodyDto.getPage(),
			searchBodyDto.getSize(), searchBodyDto.getSort());

		List<CreateRoleResponseDto> response = new ArrayList<>();
		roleList.forEach(role -> response.add(roleMapper.toResponse(role)));

		return ResponseEntity.status(HttpStatus.OK).body(roleMapper.toPaginatedResponse(response, roleList.size(),
			searchBodyDto.getPage(), searchBodyDto.getSize(), LocalDateTime.now()));
	}

	@Override
	public ResponseEntity<CreateRoleResponseDto> findRole(Long id) {
		Role role = roleUseCase.findRole(id);
		return ResponseEntity.status(HttpStatus.OK).body(roleMapper.toResponse(role));
	}

	@Override
	public ResponseEntity<CreateRoleResponseDto> updateRole(Long id, UpdateRoleDto updateRoleDto) {
		updateRoleDto.setId(id);
		Role role = roleUseCase.updateRole(roleMapper.toEntity(updateRoleDto));
		return ResponseEntity.status(HttpStatus.OK).body(roleMapper.toResponse(role));
	}

	@Override
	public ResponseEntity<DeleteResponseDto> removeRole(Long id) {
		roleUseCase.deleteRole(id);
		return new ResponseEntity<>(new DeleteResponseDto(true, 1), HttpStatus.OK);
	}
}
