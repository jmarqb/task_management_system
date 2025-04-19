package com.ms.auth.infrastructure.adapters.output.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.ms.auth.infrastructure.adapters.output.persistence.model.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	@Query("SELECT r FROM RoleEntity r WHERE r.deleted = false")
	List<RoleEntity> searchAll(Pageable pageable);

	@Query("SELECT r FROM RoleEntity r WHERE r.deleted = false AND lower(r.name) LIKE lower(concat('%', ?1, '%'))")
	List<RoleEntity> searchAllByRegex(String name, Pageable pageable);

	RoleEntity findByIdAndDeletedFalse(Long id);

	@Query("SELECT r FROM RoleEntity r WHERE r.deleted = false AND r.name = ?1")
	Optional<RoleEntity> findByName(String name);

}
