package com.ms.auth.application.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.exceptions.RoleNotFoundException;
import com.ms.auth.application.ports.input.RoleUseCase;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.ports.output.persistence.RolePersistencePort;

@RequiredArgsConstructor
@Component
public class RoleUseCaseImpl implements RoleUseCase {

	private final RolePersistencePort rolePersistencePort;

	@Override
	public Role save(Role role) {
		return rolePersistencePort.save(role);
	}

	@Override
	public List<Role> search(String search, int page, int size, String sort) {
		List<Role> roles;

		Pageable pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(sort) ?
			Sort.Direction.ASC : Sort.Direction.DESC, "id");

		roles = search != null ? rolePersistencePort.searchAllByRegex(search, pageable)
			: rolePersistencePort.searchAll(pageable);

		return roles;
	}

	@Override
	public Role findRole(Long id) {
		return existsRole(id);
	}

	@Override
	public Role updateRole(Role dataToUpdateRole) {
		Role actualRole = existsRole(dataToUpdateRole.getId());
		updateRoleFields(actualRole, dataToUpdateRole);
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

	private void updateRoleFields(Role actualRole, Role dataToUpdateRole) {
		if (dataToUpdateRole.getName() != null) actualRole.setName(dataToUpdateRole.getName());
		if (dataToUpdateRole.getDescription() != null) actualRole.setDescription(dataToUpdateRole.getDescription());
		if (dataToUpdateRole.getIcon() != null) actualRole.setIcon(dataToUpdateRole.getIcon());
		if (dataToUpdateRole.isAdmin()) actualRole.setAdmin(true);
		if (dataToUpdateRole.isDefaultRole()) actualRole.setDefaultRole(true);
	}
}
