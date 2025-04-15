package com.ms.auth.domain.ports.output.persistence;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import com.ms.auth.domain.model.Role;

public interface RolePersistencePort {

	Role save(Role role);

	List<Role> searchAll(Pageable pageable);

	List<Role> searchAllByRegex(String name, Pageable pageable);

	Role findByIdAndDeletedFalse(Long id);

	Optional<Role> findByName(String name);

}
