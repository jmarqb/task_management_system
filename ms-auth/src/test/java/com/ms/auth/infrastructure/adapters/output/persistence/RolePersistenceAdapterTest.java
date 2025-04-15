package com.ms.auth.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ms.auth.domain.model.Role;
import com.ms.auth.infrastructure.adapters.output.persistence.mapper.RolePersistenceMapper;
import com.ms.auth.infrastructure.adapters.output.persistence.model.RoleEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.RoleRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolePersistenceAdapterTest {

	private @Mock RoleRepository roleRepository;

	private @Mock RolePersistenceMapper rolePersistenceMapper;

	private @InjectMocks RolePersistenceAdapter rolePersistenceAdapter;

	private RoleEntity roleEntity;

	private Role role;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		role = Instancio.of(Role.class)
			.set(field(Role::getId), 1L)
			.set(field(Role::getName), "name")
			.set(field(Role::getDescription), "description")
			.set(field(Role::isDeleted), false)
			.set(field(Role::getDeletedAt), null)
			.supply(field(Role::getUsers), () -> new ArrayList<>())
			.create();

		roleEntity = Instancio.of(RoleEntity.class)
			.set(field(RoleEntity::getId), role.getId())
			.set(field(RoleEntity::getName), role.getName())
			.set(field(RoleEntity::getDescription), role.getDescription())
			.set(field(RoleEntity::isDeleted), role.isDeleted())
			.set(field(RoleEntity::getDeletedAt), role.getDeletedAt())
			.create();

		pageable = PageRequest.of(0, 20, Sort.Direction.ASC, "id");
	}

	@Test
	void save() {
		when(rolePersistenceMapper.toEntity(role)).thenReturn(roleEntity);
		when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
		when(rolePersistenceMapper.toDomain(roleEntity)).thenReturn(role);

		Role result = rolePersistenceAdapter.save(role);

		assertThat(result).isEqualTo(role);
		verify(rolePersistenceMapper).toEntity(role);
		verify(roleRepository).save(roleEntity);
		verify(rolePersistenceMapper).toDomain(roleEntity);
	}

	@Test
	void searchAll() {
		when(roleRepository.searchAll(pageable)).thenReturn(List.of(roleEntity, roleEntity));
		when(rolePersistenceMapper.toRoleList(List.of(roleEntity, roleEntity))).thenReturn(List.of(role, role));

		List<Role> result = rolePersistenceAdapter.searchAll(pageable);

		assertThat(result).isEqualTo(List.of(role, role));

		verify(roleRepository).searchAll(pageable);
		verify(rolePersistenceMapper).toRoleList(List.of(roleEntity, roleEntity));
	}

	@Test
	void searchAllByRegex() {
		when(roleRepository.searchAllByRegex("nameRole", pageable)).thenReturn(List.of(roleEntity, roleEntity));
		when(rolePersistenceMapper.toRoleList(List.of(roleEntity, roleEntity))).thenReturn(List.of(role, role));

		List<Role> result = rolePersistenceAdapter.searchAllByRegex("nameRole", pageable);

		assertThat(result).isEqualTo(List.of(role, role));

		verify(roleRepository).searchAllByRegex("nameRole", pageable);
		verify(rolePersistenceMapper).toRoleList(List.of(roleEntity, roleEntity));
	}

	@Test
	void findByIdAndDeletedFalse() {
		when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(roleEntity);
		when(rolePersistenceMapper.toDomain(roleEntity)).thenReturn(role);

		Role result = rolePersistenceAdapter.findByIdAndDeletedFalse(role.getId());

		assertThat(result).isEqualTo(role);

		verify(roleRepository).findByIdAndDeletedFalse(role.getId());
		verify(rolePersistenceMapper).toDomain(roleEntity);
	}

	@Test
	void findByName() {
		when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(roleEntity));
		when(rolePersistenceMapper.toDomain(roleEntity)).thenReturn(role);

		Optional<Role> result = rolePersistenceAdapter.findByName(role.getName());

		assertThat(result).isEqualTo(Optional.of(role));

		verify(roleRepository).findByName(role.getName());
		verify(rolePersistenceMapper).toDomain(roleEntity);
	}
}