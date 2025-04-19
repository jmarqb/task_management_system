package com.ms.auth.infrastructure.adapters.output.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<UserEntity> findByEmail(String email);

	@Query("SELECT r FROM UserEntity r WHERE r.deleted = false")
	List<UserEntity> searchAll(Pageable pageable);

	@Query("SELECT r FROM UserEntity r WHERE r.deleted = false AND lower(r.email) LIKE lower(concat('%', ?1, '%')) " +
		"OR lower(r.firstName) LIKE lower(concat('%', ?1, '%')) OR lower(r.lastName) LIKE lower(concat('%', ?1, '%'))")
	List<UserEntity> searchAllByRegex(String regex, Pageable pageable);

	Optional<UserEntity> findByIdAndDeletedFalse(Long id);

	@Query("SELECT r FROM UserEntity r WHERE r.deleted = false AND r.email = ?1")
	Optional<UserEntity> findByUsername(String username);


	@Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
	Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);
}
