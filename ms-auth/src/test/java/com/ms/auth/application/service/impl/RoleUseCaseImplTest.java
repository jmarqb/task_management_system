package com.ms.auth.application.service.impl;

import java.util.List;

import com.ms.auth.application.exceptions.RoleNotFoundException;
import com.ms.auth.application.impl.RoleUseCaseImpl;
import com.ms.auth.application.mapper.UpdateFieldMapper;
import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.ports.output.persistence.RolePersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.ms.auth.data.Data.createRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseImplTest {


	private @Mock RolePersistencePort rolePersistencePort;

	private @Mock UpdateFieldMapper updateFieldMapper;

	private @InjectMocks RoleUseCaseImpl roleUseCaseImpl;

	private final Pagination pagination = new Pagination(0, 10, "ASC", "id");

	@Test
	void save() {
		Role inputRole = createRole(1L);

		when(rolePersistencePort.save(any(Role.class))).thenReturn(inputRole);

		Role savedRole = roleUseCaseImpl.save(inputRole);

		assertThat(savedRole.getId()).isEqualTo(inputRole.getId());
		assertThat(savedRole.getName()).isEqualTo(inputRole.getName());
		assertThat(savedRole.getDescription()).isEqualTo(inputRole.getDescription());
		assertThat(savedRole.getIcon()).isEqualTo(inputRole.getIcon());
		assertThat(savedRole.isAdmin()).isEqualTo(inputRole.isAdmin());
		assertThat(savedRole.isDefaultRole()).isEqualTo(inputRole.isDefaultRole());
		assertThat(savedRole.isDeleted()).isEqualTo(inputRole.isDeleted());
		assertThat(savedRole.getDeletedAt()).isEqualTo(inputRole.getDeletedAt());

		verify(rolePersistencePort).save(savedRole);
	}

	@Test
	void searchContainsRegex() {
		Role inputRole = createRole(1L);
		String searchRegex = inputRole.getName().substring(0, 3);

		int page = 0;
		int size = 10;
		String sort = "ASC";

		List<Role> expectedRoles = List.of(inputRole);

		when(rolePersistencePort
			.searchAllByRegex(searchRegex, pagination))
			.thenReturn(expectedRoles);

		List<Role> actualRoles = roleUseCaseImpl.search(searchRegex, page, size, sort);

		assertThat(actualRoles).isEqualTo(expectedRoles);
		verify(rolePersistencePort).searchAllByRegex(searchRegex, pagination);
	}

	@Test
	void searchNotRegex() {
		Role inputRole = createRole(1L);
		int page = 0;
		int size = 10;
		String sort = "ASC";

		List<Role> expectedRoles = List.of(inputRole);

		when(rolePersistencePort
			.searchAll(pagination))
			.thenReturn(expectedRoles);

		List<Role> actualRoles = roleUseCaseImpl.search(null, page, size, sort);


		assertThat(actualRoles).isEqualTo(expectedRoles);
		verify(rolePersistencePort).searchAll(pagination);
	}

	@Test
	void findRole() {
		Role inputRole = createRole(1L);

		when(rolePersistencePort.findByIdAndDeletedFalse(inputRole.getId()))
			.thenReturn(inputRole);

		Role actualRole = roleUseCaseImpl.findRole(inputRole.getId());

		assertThat(actualRole).isEqualTo(inputRole);
		verify(rolePersistencePort).findByIdAndDeletedFalse(inputRole.getId());
	}

	@Test
	void findRoleNotFound() {
		Long someUid = 1L;

		when(rolePersistencePort.findByIdAndDeletedFalse(someUid)).thenThrow(
			new RoleNotFoundException("Role with %s not found".formatted(someUid)));

		RoleNotFoundException exception = assertThrows(
			RoleNotFoundException.class, () -> roleUseCaseImpl.findRole(someUid));

		assertThat(exception.getMessage()).isEqualTo("Role with %s not found".formatted(someUid));
		verify(rolePersistencePort).findByIdAndDeletedFalse(someUid);
	}

	@Test
	void updateRole() {
		Role dataToUpdateRole = createRole(1L);
		Long id = dataToUpdateRole.getId();

		Role role = Instancio.create(Role.class);
		role.setId(id);
		role.setName(dataToUpdateRole.getName());
		role.setDescription(dataToUpdateRole.getDescription());

		when(rolePersistencePort.findByIdAndDeletedFalse(id))
			.thenReturn(role);

		doNothing().when(updateFieldMapper).updateRole(dataToUpdateRole, role);

		when(rolePersistencePort.save(role)).thenReturn(role);

		Role actualRole = roleUseCaseImpl.updateRole(dataToUpdateRole);

		assertThat(actualRole).isEqualTo(role);
		verify(rolePersistencePort).findByIdAndDeletedFalse(id);
		verify(rolePersistencePort).save(role);
	}

	@Test
	void updateRoleNotFound() {
		Role dataToUpdateRole = createRole(1L);

		when(rolePersistencePort.findByIdAndDeletedFalse(dataToUpdateRole.getId())).thenThrow(
			new RoleNotFoundException("Role with %s not found".formatted(dataToUpdateRole.getId())));

		RoleNotFoundException exception = assertThrows(
			RoleNotFoundException.class, () -> roleUseCaseImpl.updateRole(dataToUpdateRole));

		assertThat(exception.getMessage()).isEqualTo("Role with %s not found".formatted(dataToUpdateRole.getId()));
		verify(rolePersistencePort).findByIdAndDeletedFalse(dataToUpdateRole.getId());
	}

	@Test
	void deleteRole() {
		Role role = createRole(1L);

		when(rolePersistencePort.findByIdAndDeletedFalse(role.getId()))
			.thenReturn(role);

		roleUseCaseImpl.deleteRole(role.getId());

		assertThat(role.isDeleted()).isTrue();
		assertThat(role.getDeletedAt()).isNotNull();

		verify(rolePersistencePort).findByIdAndDeletedFalse(role.getId());
		verify(rolePersistencePort).save(role);
	}

	@Test
	void deleteRoleNotFound() {
		Long someUid = 1L;

		when(rolePersistencePort.findByIdAndDeletedFalse(someUid)).thenThrow(
			new RoleNotFoundException("Role with %s not found".formatted(someUid)));

		RoleNotFoundException exception = assertThrows(
			RoleNotFoundException.class, () -> roleUseCaseImpl.deleteRole(someUid));

		assertThat(exception.getMessage()).isEqualTo("Role with %s not found".formatted(someUid));
		verify(rolePersistencePort).findByIdAndDeletedFalse(someUid);
	}

	@Test
	void existRole() {
		Long someUid = 1L;
		when(rolePersistencePort.findByIdAndDeletedFalse(someUid)).thenReturn(null);

		RoleNotFoundException exception = assertThrows(
			RoleNotFoundException.class, () -> roleUseCaseImpl.existsRole(someUid));

		assertThat(exception.getMessage()).isEqualTo("The role does not exist");
		verify(rolePersistencePort).findByIdAndDeletedFalse(someUid);
	}
}