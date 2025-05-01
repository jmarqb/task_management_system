package com.ms.auth.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.ports.output.persistence.RolePersistencePort;
import com.ms.auth.infrastructure.adapters.output.persistence.mapper.RolePersistenceMapper;
import com.ms.auth.infrastructure.adapters.output.persistence.model.RoleEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.RoleRepository;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RolePersistencePort {

	private final RoleRepository roleRepository;

	private final RolePersistenceMapper rolePersistenceMapper;

	@Transactional
	@Override
	public Role save(Role role) {
		RoleEntity roleEntity = rolePersistenceMapper.toEntity(role);
		RoleEntity saved = roleRepository.save(roleEntity);
		return rolePersistenceMapper.toDomain(saved);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Role> searchAll(Pagination pagination) {
		return this.rolePersistenceMapper.toRoleList(roleRepository.searchAll(buildPageable(pagination)));
	}

	@Transactional(readOnly = true)
	@Override
	public List<Role> searchAllByRegex(String name, Pagination pagination) {
		return rolePersistenceMapper.toRoleList(roleRepository.searchAllByRegex(name, buildPageable(pagination)));
	}

	@Transactional(readOnly = true)
	@Override
	public Role findByIdAndDeletedFalse(Long id) {
		return rolePersistenceMapper.toDomain(roleRepository.findByIdAndDeletedFalse(id));
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Role> findByName(String name) {
		return roleRepository.findByName(name).map(rolePersistenceMapper::toDomain);
	}

	private Pageable buildPageable(Pagination pagination) {
		return PageRequest.of(pagination.page(), pagination.size(), "asc".equalsIgnoreCase(pagination.sort()) ?
			Sort.Direction.ASC : Sort.Direction.DESC, pagination.sortBy());
	}
}
