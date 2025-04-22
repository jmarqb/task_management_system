package com.ms.auth.domain.ports.output.persistence;

import java.util.List;
import java.util.Optional;

import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.Role;

public interface RolePersistencePort {

	Role save(Role role);

	List<Role> searchAll(Pagination pagination);

	List<Role> searchAllByRegex(String name, Pagination pagination);

	Role findByIdAndDeletedFalse(Long id);

	Optional<Role> findByName(String name);

}
