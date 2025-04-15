package com.ms.auth.application.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.ms.auth.application.ports.input.UserUseCase;
import com.ms.auth.application.exceptions.RoleNotFoundException;
import com.ms.auth.application.exceptions.UserNotFoundException;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.model.User;
import com.ms.auth.domain.ports.output.persistence.RolePersistencePort;
import com.ms.auth.domain.ports.output.persistence.UserPersistencePort;

@Component
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

	private final UserPersistencePort userPersistencePort;

	private final RolePersistencePort rolePersistencePort;

	private final PasswordEncoder passwordEncoder;

	@Override
	public User save(User user) {
		Optional<Role> role = rolePersistencePort.findByName("USER");
		if (role.isPresent()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			if (user.getRoles() == null) {
				user.setRoles(new ArrayList<>());
			}
			user.getRoles().add(role.get());
			return userPersistencePort.save(user);
		} else {
			throw new RuntimeException("Default role ROLE_USER not found.");
		}
	}

	@Override
	public List<User> search(String search, int page, int size, String sort) {
		List<User> users;

		Pageable pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(sort) ?
			Sort.Direction.ASC : Sort.Direction.DESC, "id");

		users = search != null ? userPersistencePort.searchAllByRegex(search, pageable)
			: userPersistencePort.searchAll(pageable);

		return users;
	}

	@Override
	public User findUser(Long id) {
		return existsUser(id);
	}

	@Override
	public User updateUser(User dataToUpdateUser) {
		User actualUser = existsUser(dataToUpdateUser.getId());
		updateUserFields(actualUser, dataToUpdateUser);
		return userPersistencePort.save(actualUser);
	}

	@Override
	public void deleteUser(Long id) {
		User user = existsUser(id);
		user.setDeleted(true);
		user.setDeletedAt(LocalDateTime.now());
		userPersistencePort.save(user);
	}

	@Override
	public List<User> addRoleToManyUsers(Long[] usersId, Long roleId) {
		Role role = existsRole(roleId);
		List<User> users = getExistingUsers(Arrays.asList(usersId));
		addRoleToList(users, role);
		return userPersistencePort.saveAll(users);
	}

	@Override
	public List<User> removeRoleToManyUsers(Long[] usersId, Long roleId) {
		Role role = existsRole(roleId);
		List<User> users = getExistingUsers(Arrays.asList(usersId));
		removeRoleToList(users, role);
		return userPersistencePort.saveAll(users);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userPersistencePort.existsByEmail(email);
	}

	@Override
	public boolean existsByPhone(String phone) {
		return userPersistencePort.existsByPhone(phone);
	}

	private Role existsRole(Long id) {
		Role role = rolePersistencePort.findByIdAndDeletedFalse(id);
		if (role == null) {
			throw new RoleNotFoundException("The role does not exist");
		}
		return role;
	}

	private List<User> getExistingUsers(List<Long> ids) {
		List<User> users = userPersistencePort.findAllById(ids);
		if (users.isEmpty()) {
			throw new UserNotFoundException("The users do not exist");
		}
		return users;
	}

	private void addRoleToList(List<User> users, Role role) {
		users.forEach(user -> {
			user.getRoles().add(role);
		});
	}

	private void removeRoleToList(List<User> users, Role role) {
		for (User user : users) {
			user.getRoles().removeIf(r -> r.getName().equals(role.getName()));
		}
	}

	public User existsUser(Long id) {
		User user = userPersistencePort.findByIdAndDeletedFalse(id);
		if (user == null) {
			throw new UserNotFoundException("The user does not exist");
		}
		return user;
	}

	private void updateUserFields(User actualUser, User dataToUpdateUser) {
		if (dataToUpdateUser.getFirstName() != null) actualUser.setFirstName(dataToUpdateUser.getFirstName());
		if (dataToUpdateUser.getLastName() != null) actualUser.setLastName(dataToUpdateUser.getLastName());
		if (dataToUpdateUser.getEmail() != null) actualUser.setEmail(dataToUpdateUser.getEmail());
		if (dataToUpdateUser.getPhone() != null) actualUser.setPhone(dataToUpdateUser.getPhone());
		if (dataToUpdateUser.getGender() != null) actualUser.setGender(dataToUpdateUser.getGender());
		if (dataToUpdateUser.getCountry() != null) actualUser.setCountry(dataToUpdateUser.getCountry());

	}
}
