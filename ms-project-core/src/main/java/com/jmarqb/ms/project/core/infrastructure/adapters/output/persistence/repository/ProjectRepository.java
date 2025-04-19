package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

	ProjectEntity save(Project project);


	@Query("""
                SELECT DISTINCT p
                FROM ProjectEntity p
                LEFT JOIN p.members m
                WHERE p.deleted = false
                  AND (p.ownerId = :userId OR m.userId = :userId)
            """)
	List<ProjectEntity> searchAll(Pageable pageable, Long userId);

	@Query("SELECT p FROM ProjectEntity p WHERE p.uid = :uid AND p.deleted = false")
	ProjectEntity findByUid(String uid);


}
