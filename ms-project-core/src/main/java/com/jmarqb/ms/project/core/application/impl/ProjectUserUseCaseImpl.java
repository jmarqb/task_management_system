package com.jmarqb.ms.project.core.application.impl;

import com.jmarqb.ms.project.core.application.exceptions.ProjectUserNotFoundException;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUserUseCase;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectUserUseCaseImpl implements ProjectUserUseCase {

	private final ProjectUserPersistencePort projectUserPersistencePort;

	@Override
	public ProjectUser save(ProjectUser projectUser) {
		projectUser.setUid(UUID.randomUUID().toString());
		return projectUserPersistencePort.save(projectUser);
	}

	@Override
	public ProjectUser findByUid(String uid) {
		ProjectUser projectUser = projectUserPersistencePort.findByUid(uid);
		if (projectUser == null) {
			throw new ProjectUserNotFoundException("ProjectUser with id %s not found".formatted(uid));
		}
		return projectUser;
	}

	@Override
	public ProjectUser findByProjectUidAndUserId(String projectUid, Long userId) {
		ProjectUser projectUser = projectUserPersistencePort.findByProjectUidAndUserId(projectUid, userId);
		if (projectUser == null) {
			throw new ProjectUserNotFoundException("Project with id %s not found for user with id %s"
				.formatted(projectUid, userId));


		}
		return projectUser;
	}

	@Override
	public void delete(String uid) {
		ProjectUser projectUser = this.findByUid(uid);
		projectUser.setDeleted(true);
		projectUser.setDeletedAt(LocalDateTime.now());
		projectUserPersistencePort.save(projectUser);
	}

	public boolean existsByProjectUidAndUserId(String projectUid, Long userId) {
		return projectUserPersistencePort.existsByProjectUidAndUserId(projectUid, userId);
	}
}
