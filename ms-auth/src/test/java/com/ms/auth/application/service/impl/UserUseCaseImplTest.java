package com.ms.auth.application.service.impl;

import com.ms.auth.application.impl.UserUseCaseImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ms.auth.application.exceptions.DuplicateKeyException;
import com.ms.auth.application.exceptions.RoleNotFoundException;
import com.ms.auth.application.exceptions.UserNotFoundException;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.model.User;
import com.ms.auth.domain.ports.output.persistence.RolePersistencePort;
import com.ms.auth.domain.ports.output.persistence.UserPersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.ms.auth.data.Data.createRole;
import static com.ms.auth.data.Data.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

	private @Mock UserPersistencePort userPersistencePort;

	private @Mock RolePersistencePort rolePersistencePort;

	private @Mock PasswordEncoder passwordEncoder;

	private @InjectMocks UserUseCaseImpl userUseCaseImpl;

	@Test
	void save() {
		User inputUser = createUser(1L);
		inputUser.setPassword(passwordEncoder.encode(inputUser.getPassword()));

		Role role = createRole(inputUser.getId());

		when(rolePersistencePort.findByName("USER")).thenReturn(Optional.of(role));
		when(userPersistencePort.save(inputUser)).thenReturn(inputUser);

		User response = userUseCaseImpl.save(inputUser);

		assertEquals(inputUser.getId(), response.getId());
		assertEquals(inputUser.getFirstName(), response.getFirstName());
		assertEquals(inputUser.getLastName(), response.getLastName());
		assertEquals(inputUser.getEmail(), response.getEmail());
		assertEquals(inputUser.getPhone(), response.getPhone());
		assertEquals(inputUser.getRoles(), response.getRoles());

		verify(rolePersistencePort).findByName("USER");
		verify(userPersistencePort).save(inputUser);
	}

	@Test
	void save_shouldThrowRuntimeException_whenRoleNotFound() {
		User inputUser = createUser(1L);

		when(rolePersistencePort.findByName("USER")).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(
			RuntimeException.class,
			() -> userUseCaseImpl.save(inputUser)
		);

		assertEquals("Default role ROLE_USER not found.", exception.getMessage());

		verify(rolePersistencePort).findByName("USER");

		verifyNoInteractions(userPersistencePort);
	}

	@Test
	void searchContainsRegex() {
		User inputUser = createUser(1L);
		String searchRegex = inputUser.getFirstName().substring(0, 3);

		int page = 0;
		int size = 10;
		String sort = "ASC";

		List<User> expectedUsers = List.of(inputUser);

		when(userPersistencePort
			.searchAllByRegex(searchRegex, PageRequest.of(page, size, Sort.Direction.ASC, "id")))
			.thenReturn(expectedUsers);

		List<User> actualUsers = userUseCaseImpl.search(searchRegex, page, size, sort);

		assertThat(actualUsers).isEqualTo(expectedUsers);
		verify(userPersistencePort).searchAllByRegex(searchRegex, PageRequest.of(page, size, Sort.Direction.ASC, "id"));
	}

	@Test
	void searchNotRegex() {
		User inputUser = createUser(1L);

		int page = 0;
		int size = 10;
		String sort = "ASC";

		List<User> expectedUsers = List.of(inputUser);

		when(userPersistencePort
			.searchAll(PageRequest.of(page, size, Sort.Direction.ASC, "id")))
			.thenReturn(expectedUsers);

		List<User> actualUsers = userUseCaseImpl.search(null, page, size, sort);


		assertThat(actualUsers).isEqualTo(expectedUsers);
		verify(userPersistencePort).searchAll(PageRequest.of(page, size, Sort.Direction.ASC, "id"));
	}

	@Test
	void search_shouldThrowRuntimeException() {
		String searchRegex = "something";
		int page = 0;
		int size = 10;
		String sort = "ASC";

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");

		when(userPersistencePort.searchAllByRegex(searchRegex, pageable))
			.thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(
			RuntimeException.class,
			() -> userUseCaseImpl.search(searchRegex, page, size, sort)
		);

		assertEquals("Database error", exception.getMessage());

		verify(userPersistencePort).searchAllByRegex(searchRegex, pageable);
	}

	@Test
	void findUser() {
		User inputUser = createUser(1L);

		when(userPersistencePort.findByIdAndDeletedFalse(inputUser.getId()))
			.thenReturn(inputUser);

		User actualUser = userUseCaseImpl.findUser(inputUser.getId());

		assertThat(actualUser).isEqualTo(inputUser);
		verify(userPersistencePort).findByIdAndDeletedFalse(inputUser.getId());
	}

	@Test
	void findUserNotFound() {
		Long someUid = 1L;

		when(userPersistencePort.findByIdAndDeletedFalse(someUid)).thenThrow(
			new UserNotFoundException("User with %s not found".formatted(someUid)));

		UserNotFoundException exception = assertThrows(
			UserNotFoundException.class, () -> userUseCaseImpl.findUser(someUid));

		assertThat(exception.getMessage()).isEqualTo("User with %s not found".formatted(someUid));
		verify(userPersistencePort).findByIdAndDeletedFalse(someUid);
	}

	@Test
	void updateUser() {
		User dataToUpdateUser = createUser(1L);
		Long id = dataToUpdateUser.getId();

		User user = Instancio.create(User.class);
		user.setId(id);
		user.setFirstName(dataToUpdateUser.getFirstName());

		when(userPersistencePort.findByIdAndDeletedFalse(id))
			.thenReturn(user);

		when(userPersistencePort.save(user)).thenReturn(user);

		User actualUser = userUseCaseImpl.updateUser(dataToUpdateUser);

		assertThat(actualUser).isEqualTo(user);
		verify(userPersistencePort).findByIdAndDeletedFalse(id);
		verify(userPersistencePort).save(user);
	}

	@Test
	void updateUserNotFound() {
		User dataToUpdateUser = createUser(1L);

		when(userPersistencePort.findByIdAndDeletedFalse(dataToUpdateUser.getId())).thenThrow(
			new UserNotFoundException("User with %s not found".formatted(dataToUpdateUser.getId())));

		UserNotFoundException exception = assertThrows(
			UserNotFoundException.class, () -> userUseCaseImpl.findUser(dataToUpdateUser.getId()));

		assertThat(exception.getMessage()).isEqualTo("User with %s not found".formatted(dataToUpdateUser.getId()));
		verify(userPersistencePort).findByIdAndDeletedFalse(dataToUpdateUser.getId());
	}

	@Test
	void deleteUser() {
		User user = createUser(1L);

		when(userPersistencePort.findByIdAndDeletedFalse(user.getId()))
			.thenReturn(user);

		userUseCaseImpl.deleteUser(user.getId());

		assertThat(user.isDeleted()).isTrue();
		assertThat(user.getDeletedAt()).isNotNull();

		verify(userPersistencePort).findByIdAndDeletedFalse(user.getId());
		verify(userPersistencePort).save(user);
	}

	@Test
	void addRoleToManyUsers() {
		Role role = createRole(1L);
		User user1 = createUser(1L);
		User user2 = createUser(2L);
		List<User> users = List.of(user1, user2);

		Long[] usersIds = {user1.getId(), user2.getId()};

		when(rolePersistencePort.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
		when(userPersistencePort.findAllById(Arrays.asList(usersIds[0], usersIds[1]))).thenReturn(users);
		when(userPersistencePort.saveAll(users)).thenReturn(users);

		List<User> response = userUseCaseImpl.addRoleToManyUsers(usersIds, role.getId());
		assertNotNull(response);
		assertEquals(2, response.size());
		assertTrue(user1.getRoles().contains(role));
		assertTrue(user2.getRoles().contains(role));

		verify(rolePersistencePort).findByIdAndDeletedFalse(role.getId());
		verify(userPersistencePort).findAllById(Arrays.asList(usersIds[0], usersIds[1]));
		verify(userPersistencePort).saveAll(users);

	}

	@Test
	void addRoleToManyUsers_shouldThrowRoleNotFoundException() {
		Long roleId = 1L;
		Long[] userIds = {1L, 2L};

		when(rolePersistencePort.findByIdAndDeletedFalse(roleId)).thenReturn(null);

		RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
			() -> userUseCaseImpl.addRoleToManyUsers(userIds, roleId)
		);

		assertEquals("The role does not exist", exception.getMessage());

		verify(rolePersistencePort).findByIdAndDeletedFalse(roleId);
		verifyNoInteractions(userPersistencePort);
	}

	@Test
	void addRoleToManyUsers_shouldThrowUserNotFoundException_whenUsersDoNotExist() {
		Long roleId = 1L;
		Role role = createRole(roleId);
		Long[] userIds = {1L, 2L};

		when(rolePersistencePort.findByIdAndDeletedFalse(roleId)).thenReturn(role);
		when(userPersistencePort.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(Collections.emptyList());

		UserNotFoundException exception = assertThrows(UserNotFoundException.class,
			() -> userUseCaseImpl.addRoleToManyUsers(userIds, roleId)
		);

		assertEquals("The users do not exist", exception.getMessage());

		verify(rolePersistencePort).findByIdAndDeletedFalse(roleId);
		verify(userPersistencePort).findAllById(Arrays.asList(userIds[0], userIds[1]));
	}

	@Test
	void addRoleToManyUsers_shouldThrowDuplicateKeyException() {
		Role role = createRole(1L);
		User user1 = createUser(1L);
		User user2 = createUser(2L);
		user1.getRoles().add(role);
		List<User> users = List.of(user1, user2);

		Long[] userIds = {user1.getId(), user2.getId()};

		when(rolePersistencePort.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
		when(userPersistencePort.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(users);
		doThrow(new DuplicateKeyException("The user already has this role"))
			.when(userPersistencePort).saveAll(users);

		DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
			() -> userUseCaseImpl.addRoleToManyUsers(userIds, role.getId())
		);

		assertEquals("The user already has this role", exception.getMessage());

		verify(rolePersistencePort).findByIdAndDeletedFalse(role.getId());
		verify(userPersistencePort).findAllById(Arrays.asList(userIds[0], userIds[1]));
		verify(userPersistencePort).saveAll(users);
	}

	@Test
	void removeRoleToManyUsers_shouldThrowRoleNotFoundException() {
		Long roleId = 1L;
		Long[] userIds = {1L, 2L};

		when(rolePersistencePort.findByIdAndDeletedFalse(roleId)).thenReturn(null);

		RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
			() -> userUseCaseImpl.removeRoleToManyUsers(userIds, roleId)
		);

		assertEquals("The role does not exist", exception.getMessage());

		verify(rolePersistencePort).findByIdAndDeletedFalse(roleId);
		verifyNoInteractions(userPersistencePort);
	}

	@Test
	void removeRoleToManyUsers_shouldThrowUserNotFoundException() {
		Long roleId = 1L;
		Role role = createRole(roleId);
		Long[] userIds = {1L, 2L};

		when(rolePersistencePort.findByIdAndDeletedFalse(roleId)).thenReturn(role);
		when(userPersistencePort.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(Collections.emptyList());

		UserNotFoundException exception = assertThrows(UserNotFoundException.class,
			() -> userUseCaseImpl.removeRoleToManyUsers(userIds, roleId)
		);

		assertEquals("The users do not exist", exception.getMessage());

		verify(rolePersistencePort).findByIdAndDeletedFalse(roleId);
		verify(userPersistencePort).findAllById(Arrays.asList(userIds[0], userIds[1]));
	}

	@Test
	void removeRoleFromUsersEvenIfSomeDoNotHaveIt() {
		Role role = createRole(1L);
		User user1 = createUser(1L);
		User user2 = createUser(2L);
		user1.getRoles().add(role); // Only user1 has this role
		List<User> users = List.of(user1, user2);

		Long[] userIds = {user1.getId(), user2.getId()};

		when(rolePersistencePort.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
		when(userPersistencePort.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(users);
		when(userPersistencePort.saveAll(users)).thenReturn(users);

		List<User> response = userUseCaseImpl.removeRoleToManyUsers(userIds, role.getId());

		assertNotNull(response);
		assertEquals(2, response.size());
		assertFalse(user1.getRoles().contains(role));
		assertFalse(user2.getRoles().contains(role));

		verify(rolePersistencePort).findByIdAndDeletedFalse(role.getId());
		verify(userPersistencePort).findAllById(Arrays.asList(userIds[0], userIds[1]));
		verify(userPersistencePort).saveAll(users);
	}

	@Test
	void HandleUnexpectedError_whenSavingUsers() {
		Role role = createRole(1L);
		User user1 = createUser(1L);
		User user2 = createUser(2L);
		user1.getRoles().add(role);
		user2.getRoles().add(role);
		List<User> users = List.of(user1, user2);

		Long[] userIds = {user1.getId(), user2.getId()};

		when(rolePersistencePort.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
		when(userPersistencePort.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(users);
		doThrow(new RuntimeException("Unexpected error"))
			.when(userPersistencePort).saveAll(users);

		RuntimeException exception = assertThrows(RuntimeException.class,
			() -> userUseCaseImpl.removeRoleToManyUsers(userIds, role.getId())
		);

		assertEquals("Unexpected error", exception.getMessage());

		verify(rolePersistencePort).findByIdAndDeletedFalse(role.getId());
		verify(userPersistencePort).findAllById(Arrays.asList(userIds[0], userIds[1]));
		verify(userPersistencePort).saveAll(users);
	}

	@Test
	void existsByEmail_shouldReturnTrue() {
		String email = "test@example.com";

		when(userPersistencePort.existsByEmail(email)).thenReturn(true);

		boolean result = userUseCaseImpl.existsByEmail(email);

		assertTrue(result);
		verify(userPersistencePort).existsByEmail(email);
	}

	@Test
	void existsByEmail_shouldReturnFalse() {
		String email = "nonexistent@example.com";

		when(userPersistencePort.existsByEmail(email)).thenReturn(false);

		boolean result = userUseCaseImpl.existsByEmail(email);

		assertFalse(result);
		verify(userPersistencePort).existsByEmail(email);
	}

	@Test
	void existsByPhone_shouldReturnTrue() {
		String phone = "123456789";

		when(userPersistencePort.existsByPhone(phone)).thenReturn(true);

		boolean result = userUseCaseImpl.existsByPhone(phone);

		assertTrue(result);
		verify(userPersistencePort).existsByPhone(phone);
	}

	@Test
	void existsByPhone_shouldReturnFalse() {
		String phone = "987654321";

		when(userPersistencePort.existsByPhone(phone)).thenReturn(false);

		boolean result = userUseCaseImpl.existsByPhone(phone);

		assertFalse(result);
		verify(userPersistencePort).existsByPhone(phone);
	}

	@Test
	void existUser() {
		Long someUid = 1L;
		when(userPersistencePort.findByIdAndDeletedFalse(someUid)).thenReturn(null);

		UserNotFoundException exception = assertThrows(
			UserNotFoundException.class, () -> userUseCaseImpl.existsUser(someUid));

		assertThat(exception.getMessage()).isEqualTo("The user does not exist");
		verify(userPersistencePort).findByIdAndDeletedFalse(someUid);
	}
}