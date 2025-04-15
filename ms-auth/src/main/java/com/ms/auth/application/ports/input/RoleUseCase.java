package com.ms.auth.application.ports.input;

import java.util.List;

import com.ms.auth.domain.model.Role;

public interface RoleUseCase {
	Role save(Role role);

	List<Role> search(String search, int page, int size, String sort);

	Role findRole(Long id);

	Role updateRole(Role role);

	void deleteRole(Long id);
}
