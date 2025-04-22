package com.jmarqb.ms.project.core.application.impl;

import com.jmarqb.ms.project.core.application.exceptions.InvalidProjectForUserException;
import com.jmarqb.ms.project.core.application.exceptions.ProjectNotFoundException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedProjectException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedTaskAccessException;
import com.jmarqb.ms.project.core.application.mapper.UpdateFieldMapper;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUseCase;
import com.jmarqb.ms.project.core.application.vo.ProjectUserRole;
import com.jmarqb.ms.project.core.domain.model.Pagination;
import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectPersistencePort;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.TaskPersistencePort;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectUseCaseImpl implements ProjectUseCase {

	private final ProjectPersistencePort projectPersistencePort;

	private final ProjectUserPersistencePort projectUserPersistencePort;
	private final TaskPersistencePort taskPersistencePort;

	private final UpdateFieldMapper updateFieldMapper;

	@Override
	public Project save(Project project) {
		project.setUid(UUID.randomUUID().toString());

		Project savedProject = projectPersistencePort.save(project);
		ProjectUser projectUser = ProjectUser.builder()
			.uid(UUID.randomUUID().toString())
			.project(savedProject)
			.userId(savedProject.getOwnerId())
			.role(ProjectUserRole.OWNER.name())
			.deleted(false)
			.deletedAt(null)
			.build();
		projectUserPersistencePort.save(projectUser);
		return savedProject;
	}

	public Project findProjectByUid(String uid) {
		try {
			return projectPersistencePort.findByUid(uid);
		} catch (ProjectNotFoundException e) {
			log.warn(e.getMessage(), uid);
			throw e;
		}
	}


	@Override
	public List<Project> searchAll(int page, int size, String sort, Long userId) {
		List<Project> projects;

		Pagination pagination = new Pagination(page, size, sort, "id");

		projects = projectPersistencePort.searchAll(pagination, userId);

		return projects;
	}

	@Override
	public Project updateProject(Project dataToUpdateProject, Long userId) {
		Project actualProject = this.findProjectByUid(dataToUpdateProject.getUid());
		if (!Objects.equals(actualProject.getOwnerId(), userId)) {
			throw new UnauthorizedProjectException("Only the project owner can update the project.");
		}
		updateFieldMapper.updateProject(dataToUpdateProject, actualProject);
		return projectPersistencePort.save(actualProject);
	}

	@Override
	public Project addMembersToProject(String projectUid, List<Long> userIds, Long requesterUserId) {
		if (userIds == null || userIds.isEmpty()) {
			throw new IllegalArgumentException("The list of users must not be empty");
		}
		Project project = this.findProjectByUid(projectUid);
		ProjectUser requester = validateProjectUser(projectUid, requesterUserId);

		ProjectUserRole requesterRole = ProjectUserRole.valueOf(requester.getRole());

		if (requesterRole != ProjectUserRole.OWNER) {
			throw new UnauthorizedTaskAccessException("Only the project owner can add members");
		}
		List<ProjectUser> projectUsersList = new ArrayList<>();
		for (Long userId : userIds) {
			boolean alreadyExists = projectUserPersistencePort.existsByProjectUidAndUserId(projectUid, userId);
			if (alreadyExists) continue;

			ProjectUser newMember = ProjectUser.builder()
				.uid(UUID.randomUUID().toString())
				.project(project)
				.userId(userId)
				.role(ProjectUserRole.MEMBER.name())
				.deleted(false)
				.deletedAt(null)
				.build();

			projectUsersList.add(newMember);
		}
		projectUsersList = projectUserPersistencePort.saveAll(projectUsersList);
		project.getMembers().addAll(projectUsersList);
		return project;
	}

	@Override
	public Project removeMemberFromProject(String projectUid, Long memberIdToRemove, Long requesterUserId) {
		Project project = findProjectByUid(projectUid);

		ProjectUser requester = validateProjectUser(projectUid, requesterUserId);
		ProjectUserRole requesterRole = ProjectUserRole.valueOf(requester.getRole());

		if (requesterRole != ProjectUserRole.OWNER) {
			throw new UnauthorizedTaskAccessException("Only the project owner can remove members");
		}

		if (Objects.equals(requesterUserId, memberIdToRemove)) {
			throw new IllegalArgumentException("The project owner cannot remove themselves");
		}

		ProjectUser memberToRemove = validateProjectUser(projectUid, memberIdToRemove);

		List<Task> memberTasks = taskPersistencePort.findByProjectUidAndAssignedUserId(projectUid, memberIdToRemove);

		for (Task task : memberTasks) {
			task.setAssignedUserId(requesterUserId);
			task.setProject(project);
			taskPersistencePort.save(task);
		}
		memberToRemove.setDeleted(true);
		memberToRemove.setDeletedAt(LocalDateTime.now());
		projectUserPersistencePort.save(memberToRemove);

		project.getMembers().stream().filter(member -> Objects.equals(member.getUid(), memberToRemove.getUid()))
			.findFirst().ifPresent(project.getMembers()::remove);
		return project;
	}

	@Override
	public void deleteProject(String uid, Long userId) {
		Project project = this.findProjectByUid(uid);
		if (!Objects.equals(project.getOwnerId(), userId)) {
			throw new UnauthorizedProjectException("Only the project owner can delete the project.");
		}
		project.setDeleted(true);
		project.setDeletedAt(LocalDateTime.now());
		projectPersistencePort.save(project);
	}

	@Override
	public boolean isArchived(String uid) {
		Project project = this.findProjectByUid(uid);
		return project.isArchived();
	}

	private ProjectUser validateProjectUser(String projectUid, Long userId) {
		ProjectUser projectUser = projectUserPersistencePort.findByProjectUidAndUserId(projectUid, userId);
		if (projectUser == null) {
			throw new InvalidProjectForUserException("Project with id %s not found for user with id %s"
				.formatted(projectUid, userId));
		}
		return projectUser;
	}
}
