package com.jmarqb.ms.project.core.application.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import com.jmarqb.ms.project.core.application.enums.ProjectUserRole;
import com.jmarqb.ms.project.core.application.exceptions.InvalidProjectForUserException;
import com.jmarqb.ms.project.core.application.exceptions.TaskNotFoundException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedTaskAccessException;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUseCase;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUserUseCase;
import com.jmarqb.ms.project.core.application.ports.input.TaskUseCase;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.TaskPersistencePort;

@Component
@RequiredArgsConstructor
public class TaskUseCaseImpl implements TaskUseCase {

	private final TaskPersistencePort taskPersistencePort;
	private final ProjectUserPersistencePort projectUserPersistencePort;


	private final ProjectUserUseCase projectUserUseCase;

	private final ProjectUseCase projectUseCase;

	@Override
	public Task save(Task task, Long userId) {
		assignTaskUser(task, userId);
		task.setUid(UUID.randomUUID().toString());
		return taskPersistencePort.save(task);
	}

	@Override
	public Task findTaskByUid(String uid) {
		Task task = taskPersistencePort.findByUid(uid);
		if (task == null) {
			throw new TaskNotFoundException("Task with id %s not found".formatted(uid));
		}
		return task;
	}

	@Override
	public List<Task> searchAll(int page, int size, String sort, Long userId) {
		List<Task> tasks;
		Pageable pageable = buildPageable(page, size, sort);
		tasks = taskPersistencePort.searchAll(pageable, userId);
		return tasks;
	}

	@Override
	public List<Task> searchAllByProjectUid(String projectUid, int page, int size, String sort) {
		List<Task> tasks;
		Pageable pageable = buildPageable(page, size, sort);
		tasks = taskPersistencePort.findByProjectUid(pageable, projectUid);
		return tasks;
	}

	@Override
	public List<Task> searchAllByAssignedUserId(Long userId, int page, int size, String sort) {
		List<Task> tasks;
		Pageable pageable = buildPageable(page, size, sort);
		tasks = taskPersistencePort.findByAssignedUserId(pageable, userId);
		return tasks;
	}

	@Override
	public List<Task> searchAllByProjectUidAndAssignedUserId(String projectUid, Long userId, int page,
																													int size, String sort) {
		List<Task> tasks;
		Pageable pageable = buildPageable(page, size, sort);
		tasks = taskPersistencePort.findByProjectUidAndAssignedUserIdPaginated(projectUid, userId, pageable);
		return tasks;
	}

	@Override
	public Task updateTask(Task dataToUpdateTask, Long userId) {
		Task actualTask = findTaskByUid(dataToUpdateTask.getUid());
		ProjectUser projectUser = validateUserCanModifyTask(actualTask, userId);
		ProjectUserRole role = ProjectUserRole.valueOf(projectUser.getRole());

		if (role == ProjectUserRole.MEMBER) {
			dataToUpdateTask.setAssignedUserId(null);
		}

		if (role == ProjectUserRole.OWNER) {
			Long newAssignedUserId = dataToUpdateTask.getAssignedUserId();
			if (newAssignedUserId != null && !Objects.equals(newAssignedUserId, actualTask.getAssignedUserId())) {
				validateProjectUser(actualTask.getProject().getUid(), newAssignedUserId);
			}
		}
		updateTaskFields(actualTask, dataToUpdateTask);
		return taskPersistencePort.save(actualTask);
	}

	@Override
	public void deleteTask(String uid, Long userId) {
		Task task = findTaskByUid(uid);
		validateUserCanModifyTask(task, userId);
		task.setDeleted(true);
		task.setDeletedAt(LocalDateTime.now());
		taskPersistencePort.save(task);
	}

	private Pageable buildPageable(int page, int size, String sort) {
		return PageRequest.of(page, size, "asc".equalsIgnoreCase(sort) ?
			Sort.Direction.ASC : Sort.Direction.DESC, "id");
	}

	private void updateTaskFields(Task actualTask, Task dataToUpdateTask) {
		if (dataToUpdateTask.getName() != null) actualTask.setName(dataToUpdateTask.getName());
		if (dataToUpdateTask.getStatus() != null)
			actualTask.setStatus(dataToUpdateTask.getStatus());
		if (dataToUpdateTask.getPriority() != null)
			actualTask.setPriority(dataToUpdateTask.getPriority());
		if (dataToUpdateTask.getAssignedUserId() != null)
			actualTask.setAssignedUserId(dataToUpdateTask.getAssignedUserId());

	}

	private void assignTaskUser(Task task, Long userId) {
		ProjectUser projectUser = validateProjectUser(task.getProject().getUid(), userId);

		validateProjectIsNotArchived(task.getProject().getUid());

		ProjectUserRole role = ProjectUserRole.valueOf(projectUser.getRole());

		if (role == ProjectUserRole.MEMBER) {
			task.setAssignedUserId(userId);
		} else if (role == ProjectUserRole.OWNER) {
			if (task.getAssignedUserId() == null) {
				throw new InvalidProjectForUserException("As owner, you must specify the assigned user for the task.");
			}
			if (!Objects.equals(task.getAssignedUserId(), userId)) {
				validateProjectUser(task.getProject().getUid(), task.getAssignedUserId());
			}
		}
	}

	private ProjectUser validateUserCanModifyTask(Task task, Long userId) {
		validateProjectIsNotArchived(task.getProject().getUid());

		ProjectUser projectUser = validateProjectUser(task.getProject().getUid(), userId);
		ProjectUserRole role = ProjectUserRole.valueOf(projectUser.getRole());

		if (role == ProjectUserRole.MEMBER && !Objects.equals(task.getAssignedUserId(), userId)) {
			throw new UnauthorizedTaskAccessException("Members can only modify their own tasks");
		}

		return projectUser;
	}

	private ProjectUser validateProjectUser(String projectUid, Long userId) {
		ProjectUser projectUser = projectUserUseCase.findByProjectUidAndUserId(projectUid, userId);
		if (projectUser == null) {
			throw new InvalidProjectForUserException("Project with id %s not found for user with id %s"
				.formatted(projectUid, userId));
		}
		return projectUser;
	}

	public void filterTaskUser(Task task, Long userId) {
		ProjectUser projectUser = projectUserPersistencePort.findByProjectUidAndUserId(task.getProject().getUid(), userId);
		if (projectUser == null) {
			throw new TaskNotFoundException("Task with id %s not found for user with id %s"
				.formatted(task.getUid(), userId));
		}

	}

	private void validateProjectIsNotArchived(String projectUid) {
		boolean isArchived = projectUseCase.isArchived(projectUid);
		if (isArchived) {
			throw new InvalidProjectForUserException(
				"Cannot assign task because project with ID %s is archived".formatted(projectUid));
		}
	}

}
