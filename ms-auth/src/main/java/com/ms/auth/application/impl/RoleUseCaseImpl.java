package com.ms.auth.application.impl;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.exceptions.RoleNotFoundException;
import com.ms.auth.application.mapper.UpdateFieldMapper;
import com.ms.auth.application.ports.input.RoleUseCase;
import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.ports.output.persistence.RolePersistencePort;

@RequiredArgsConstructor
@Component
public class RoleUseCaseImpl implements RoleUseCase {

	private final RolePersistencePort rolePersistencePort;

	private final UpdateFieldMapper updateFieldMapper;

	@Override
	public Role save(Role role) {
		return rolePersistencePort.save(role);
	}

	@Override
	public List<Role> search(String search, int page, int size, String sort) {
		List<Role> roles;

		Pagination pagination = new Pagination(page, size, sort, "id");

		roles = search != null ? rolePersistencePort.searchAllByRegex(search, pagination)
			: rolePersistencePort.searchAll(pagination);

		return roles;
	}

	@Override
	public Role findRole(Long id) {
		return existsRole(id);
	}

	@Override
	public Role updateRole(Role dataToUpdateRole) {
		Role actualRole = existsRole(dataToUpdateRole.getId());
		updateFieldMapper.updateRole(dataToUpdateRole, actualRole);
		return rolePersistencePort.save(actualRole);
	}

	@Override
	public void deleteRole(Long id) {
		Role role = existsRole(id);
		role.setDeleted(true);
		role.setDeletedAt(LocalDateTime.now());
		rolePersistencePort.save(role);
	}

	public Role existsRole(Long id) {
		Role role = rolePersistencePort.findByIdAndDeletedFalse(id);
		if (role == null) {
			throw new RoleNotFoundException("The role does not exist");
		}
		return role;
	}
}
