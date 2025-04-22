package com.jmarqb.ms.project.core.domain.ports.output.persistence;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;

import java.util.List;

public interface ProjectUserPersistencePort {
	ProjectUser save(ProjectUser projectUser);

	List<ProjectUser> saveAll(List<ProjectUser> projectUsers);

	ProjectUser findByProjectUidAndUserId(String projectUid, Long userId);

	List<ProjectUser> findByProjectUid(String projectUid);

	List<ProjectUser> findByUserId(Long userId);

	ProjectUser findByUid(String uid);

	boolean existsByProjectUidAndUserId(String projectUid, Long userId);

}
