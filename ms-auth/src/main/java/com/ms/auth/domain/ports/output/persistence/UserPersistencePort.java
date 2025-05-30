package com.ms.auth.domain.ports.output.persistence;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.User;

public interface UserPersistencePort {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	User save(User user);

	Optional<User> findByEmail(String email);

	List<User> findAllById(List<Long> ids);

	List<User> saveAll(List<User> users);

	List<User> searchAll(Pagination pagination);

	List<User> searchAllByRegex(String regex, Pagination pagination);

	User findByIdAndDeletedFalse(Long id);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmailWithRoles(@Param("email") String email);

}
