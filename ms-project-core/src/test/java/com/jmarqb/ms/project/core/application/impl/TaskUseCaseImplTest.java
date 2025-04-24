package com.jmarqb.ms.project.core.application.impl;

import com.jmarqb.ms.project.core.application.exceptions.InvalidProjectForUserException;
import com.jmarqb.ms.project.core.application.exceptions.TaskNotFoundException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedTaskAccessException;
import com.jmarqb.ms.project.core.application.mapper.UpdateFieldMapper;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUseCase;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUserUseCase;
import com.jmarqb.ms.project.core.application.vo.PriorityStatus;
import com.jmarqb.ms.project.core.application.vo.ProjectUserRole;
import com.jmarqb.ms.project.core.application.vo.TaskStatus;
import com.jmarqb.ms.project.core.domain.model.Pagination;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.TaskPersistencePort;

import java.util.List;
import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskUseCaseImplTest {

	@Mock
	private TaskPersistencePort taskPersistencePort;

	@Mock
	private ProjectUserPersistencePort projectUserPersistencePort;

	@Mock
	private ProjectUserUseCase projectUserUseCase;

	@Mock
	private ProjectUseCase projectUseCase;

	@Mock
	private UpdateFieldMapper updateFieldMapper;


	@InjectMocks
	private TaskUseCaseImpl taskUseCase;

	@Test
	void save() {
		Task task = Instancio.create(Task.class);
		ProjectUser projectUser = Instancio.create(ProjectUser.class);
		projectUser.setRole("MEMBER");

		when(projectUserUseCase.findByProjectUidAndUserId(task.getProject().getUid(), 1L)).thenReturn(projectUser);
		when(projectUseCase.isArchived(task.getProject().getUid())).thenReturn(false);
		when(taskPersistencePort.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

		Task result = taskUseCase.save(task, 1L);

		assertThat(result.getUid()).isNotNull();
		assertThat(result.getAssignedUserId()).isEqualTo(1L);
		verify(taskPersistencePort).save(result);
	}

	@Test
	void save_WithoutAssignedUser() {
		Task task = Instancio.create(Task.class);
		task.setAssignedUserId(null);
		Long userId = 1L;

		ProjectUser projectUser = Instancio.create(ProjectUser.class);
		projectUser.setRole(ProjectUserRole.OWNER.name());

		when(projectUserUseCase.findByProjectUidAndUserId(task.getProject().getUid(), userId))
			.thenReturn(projectUser);
		when(projectUseCase.isArchived(task.getProject().getUid())).thenReturn(false);

		assertThatThrownBy(() -> taskUseCase.save(task, userId))
			.isInstanceOf(InvalidProjectForUserException.class)
			.hasMessageContaining("As owner, you must specify the assigned user for the task.");
	}

	@Test
	void findTaskByUid() {
		Task expectedTask = Instancio.create(Task.class);
		when(taskPersistencePort.findByUid(expectedTask.getUid())).thenReturn(expectedTask);

		Task result = taskUseCase.findTaskByUid(expectedTask.getUid());

		assertThat(result).isEqualTo(expectedTask);
	}

	@Test
	void findTaskByUid_NotFound() {
		String uid = UUID.randomUUID().toString();
		when(taskPersistencePort.findByUid(uid)).thenReturn(null);

		assertThatThrownBy(() -> taskUseCase.findTaskByUid(uid))
			.isInstanceOf(TaskNotFoundException.class)
			.hasMessage("Task with id %s not found".formatted(uid));
	}

	@Test
	void searchAll() {
		Pagination pagination = new Pagination(0, 10, "asc", "id");
		List<Task> expected = List.of(Instancio.create(Task.class));

		when(taskPersistencePort.searchAll(eq(pagination), eq(1L))).thenReturn(expected);

		List<Task> result = taskUseCase.searchAll(0, 10, "asc", 1L);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void searchAllByProjectUid() {
		Pagination pagination = new Pagination(0, 10, "desc", "id");
		String projectUid = UUID.randomUUID().toString();
		List<Task> expected = List.of(Instancio.create(Task.class));

		when(taskPersistencePort.findByProjectUid(eq(pagination), eq(projectUid)))
			.thenReturn(expected);

		List<Task> result = taskUseCase.searchAllByProjectUid(projectUid, 0, 10, "desc");

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void searchAllByAssignedUserId() {
		List<Task> expected = List.of(Instancio.create(Task.class));

		when(taskPersistencePort.findByAssignedUserId(any(Pagination.class), eq(1L)))
			.thenReturn(expected);

		List<Task> result = taskUseCase.searchAllByAssignedUserId(1L, 0, 20, "asc");

		assertThat(result).isEqualTo(expected);
		verify(taskPersistencePort).findByAssignedUserId(any(Pagination.class), eq(1L));
	}

	@Test
	void searchAllByProjectUidAndAssignedUserId() {
		String projectUid = UUID.randomUUID().toString();
		List<Task> expected = List.of(Instancio.create(Task.class));

		when(taskPersistencePort.findByProjectUidAndAssignedUserIdPaginated(eq(projectUid), eq(1L), any(Pagination.class)))
			.thenReturn(expected);

		List<Task> result = taskUseCase.searchAllByProjectUidAndAssignedUserId(projectUid, 1L, 1, 5, "desc");

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void updateTask() {
		String uid = UUID.randomUUID().toString();
		Task updateData = Task.builder()
				.uid(uid)
				.priority(PriorityStatus.LOW.toString())
				.status(TaskStatus.PENDING.toString())
				.deleted(false)
				.deletedAt(null)
				.build();

		Task existingTask = Instancio.create(Task.class);
		existingTask.setUid(uid);
		existingTask.setAssignedUserId(1L);


		ProjectUser projectUser = Instancio.create(ProjectUser.class);
		projectUser.setRole(ProjectUserRole.MEMBER.name());
		projectUser.setProject(existingTask.getProject());
		projectUser.setUserId(existingTask.getAssignedUserId());

		when(taskPersistencePort.findByUid(existingTask.getUid())).thenReturn(existingTask);

		when(projectUserUseCase.findByProjectUidAndUserId(projectUser.getProject().getUid(), existingTask.getAssignedUserId()))
			.thenReturn(projectUser);

		when(projectUseCase.isArchived(existingTask.getProject().getUid())).thenReturn(false);

		doNothing().when(updateFieldMapper).updateTask(updateData, existingTask);

		when(taskPersistencePort.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

		Task result = taskUseCase.updateTask(updateData, existingTask.getAssignedUserId());

		assertThat(result).isEqualTo(existingTask);
		verify(taskPersistencePort).findByUid(existingTask.getUid());
		verify(projectUserUseCase).findByProjectUidAndUserId(projectUser.getProject().getUid(), existingTask.getAssignedUserId());
		verify(updateFieldMapper).updateTask(updateData, existingTask);
		verify(taskPersistencePort).save(existingTask);
	}

	@Test
	void deleteTask() {
		Task task = Instancio.create(Task.class);
		task.setDeleted(false);
		task.setDeletedAt(null);

		ProjectUser projectUser = Instancio.create(ProjectUser.class);
		projectUser.setRole("OWNER");
		task.setAssignedUserId(projectUser.getUserId());

		when(taskPersistencePort.findByUid(task.getUid())).thenReturn(task);
		when(projectUserUseCase.findByProjectUidAndUserId(task.getProject().getUid(), projectUser.getUserId())).thenReturn(projectUser);
		when(projectUseCase.isArchived(task.getProject().getUid())).thenReturn(false);

		taskUseCase.deleteTask(task.getUid(), projectUser.getUserId());

		assertThat(task.isDeleted()).isTrue();
		assertThat(task.getDeletedAt()).isNotNull();
		verify(taskPersistencePort).save(task);
	}

	@Test
	void filterTaskUser() {
		Task task = Instancio.create(Task.class);
		String uid = task.getUid();
		Long userId = 123L;

		when(projectUserPersistencePort.findByProjectUidAndUserId(task.getProject().getUid(), userId)).thenReturn(null);

		assertThatThrownBy(() -> taskUseCase.filterTaskUser(task, userId))
			.isInstanceOf(TaskNotFoundException.class)
			.hasMessage("Task with id %s not found for user with id %s".formatted(uid, userId));
	}

	@Test
	void saveThrowInvalidProjectForUserException() {
		Task task = Instancio.create(Task.class);
		Long userId = 1L;

		when(projectUserUseCase.findByProjectUidAndUserId(task.getProject().getUid(), userId))
			.thenReturn(null);

		assertThatThrownBy(() -> taskUseCase.save(task, userId))
			.isInstanceOf(InvalidProjectForUserException.class)
			.hasMessageContaining("Project with id");
	}

	@Test
	void updateTask_ProjectIsArchived_ThrowInvalidProjectForUserException() {
		Task task = Instancio.create(Task.class);
		Task updateData = new Task();
		updateData.setUid(task.getUid());

		when(taskPersistencePort.findByUid(task.getUid())).thenReturn(task);
		when(projectUseCase.isArchived(task.getProject().getUid())).thenReturn(true);

		assertThatThrownBy(() -> taskUseCase.updateTask(updateData, 1L))
			.isInstanceOf(InvalidProjectForUserException.class)
			.hasMessageContaining("project with ID");
	}

	@Test
	void updateTask_TriesToModifyAnotherUsersTask_ThrowUnauthorizedTaskAccessException() {
		Task task = Instancio.create(Task.class);
		task.setAssignedUserId(2L);

		Task updateData = new Task();
		updateData.setUid(task.getUid());
		updateData.setAssignedUserId(2L);

		ProjectUser projectUser = Instancio.create(ProjectUser.class);
		projectUser.setRole(ProjectUserRole.MEMBER.name());

		when(taskPersistencePort.findByUid(task.getUid())).thenReturn(task);
		when(projectUserUseCase.findByProjectUidAndUserId(task.getProject().getUid(), 1L))
			.thenReturn(projectUser);
		when(projectUseCase.isArchived(task.getProject().getUid())).thenReturn(false);

		assertThatThrownBy(() -> taskUseCase.updateTask(updateData, 1L))
			.isInstanceOf(UnauthorizedTaskAccessException.class)
			.hasMessageContaining("Members can only modify their own tasks");
	}


}