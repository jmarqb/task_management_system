package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectUserEntity;

public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, String> {

	ProjectUserEntity save(ProjectUser projectUser);

	@Query("SELECT p FROM ProjectUserEntity p WHERE p.project.uid = :projectUid AND p.userId = :userId AND p.deleted = false")
	ProjectUserEntity findByProjectUidAndUserId(String projectUid, Long userId);

	@Query("SELECT p FROM ProjectUserEntity p WHERE p.project.uid = :projectUid AND p.deleted = false")
	List<ProjectUserEntity> findByProjectUid(String projectUid);

	@Query("SELECT p FROM ProjectUserEntity p WHERE p.userId = :userId AND p.deleted = false")
	List<ProjectUserEntity> findByUserId(Long userId);

	@Query("SELECT p FROM ProjectUserEntity p WHERE p.uid = :uid AND p.deleted = false")
	ProjectUserEntity findByUid(String uid);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM ProjectUserEntity p WHERE p.project.uid = :projectUid AND p.userId = :userId AND p.deleted = false")
	boolean existsByProjectUidAndUserId(@Param("projectUid") String projectUid, @Param("userId") Long userId);


}
