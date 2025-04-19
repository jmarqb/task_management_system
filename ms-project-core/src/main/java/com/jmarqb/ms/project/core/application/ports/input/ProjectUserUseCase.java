package com.jmarqb.ms.project.core.application.ports.input;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;

public interface ProjectUserUseCase {

	ProjectUser save(ProjectUser projectUser);

	ProjectUser findByUid(String uid);

	ProjectUser findByProjectUidAndUserId(String projectUid, Long userId);

	void delete(String uid);

	boolean existsByProjectUidAndUserId(String projectUid, Long userId);
}
