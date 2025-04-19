package com.ms.auth.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

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

	@Override
	public Role save(Role role) {
		RoleEntity roleEntity = rolePersistenceMapper.toEntity(role);
		RoleEntity saved = roleRepository.save(roleEntity);
		return rolePersistenceMapper.toDomain(saved);
	}

	@Override
	public List<Role> searchAll(Pageable pageable) {
		return this.rolePersistenceMapper.toRoleList(roleRepository.searchAll(pageable));
	}

	@Override
	public List<Role> searchAllByRegex(String name, Pageable pageable) {
		return rolePersistenceMapper.toRoleList(roleRepository.searchAllByRegex(name, pageable));
	}

	@Override
	public Role findByIdAndDeletedFalse(Long id) {
		return rolePersistenceMapper.toDomain(roleRepository.findByIdAndDeletedFalse(id));
	}

	@Override
	public Optional<Role> findByName(String name) {
		return roleRepository.findByName(name).map(rolePersistenceMapper::toDomain);
	}
}
