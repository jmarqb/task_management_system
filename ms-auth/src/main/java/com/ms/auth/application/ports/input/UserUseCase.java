package com.ms.auth.application.ports.input;

import java.util.List;

import com.ms.auth.domain.model.User;

public interface UserUseCase {

	User save(User user);

	List<User> search(String search, int page, int size, String sort);

	User findUser(Long id);


	User updateUser(User user);

	void deleteUser(Long id);

	List<User> addRoleToManyUsers(Long[] usersId, Long roleId);

	List<User> removeRoleToManyUsers(Long[] usersId, Long roleId);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
}
