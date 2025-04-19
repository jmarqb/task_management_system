package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jmarqb.ms.project.core.application.enums.PriorityStatus;
import com.jmarqb.ms.project.core.application.enums.TaskStatus;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.TaskPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.TaskEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.TaskRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskPersistenceAdapterTest {

	private @Mock TaskRepository taskRepository;
	private @Mock TaskPersistenceMapper taskPersistenceMapper;
	private @InjectMocks TaskPersistenceAdapter taskPersistenceAdapter;

	private TaskEntity taskEntity;
	private Task task;
	private ProjectEntity projectEntity;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		projectEntity = Instancio.of(ProjectEntity.class)
			.set(field(ProjectEntity::getUid), UUID.randomUUID().toString())
			.set(field(ProjectEntity::isDeleted), false)
			.set(field(ProjectEntity::getDeletedAt), null)
			.set(field(ProjectEntity::getMembers), new ArrayList<>())
			.set(field(ProjectEntity::getTasks), new ArrayList<>())
			.create();

		task = Instancio.of(Task.class)
			.set(field(Task::getUid), UUID.randomUUID().toString())
			.set(field(Task::getName), "Sample Task")
			.set(field(Task::getStatus), TaskStatus.PENDING.toString())
			.set(field(Task::getPriority), PriorityStatus.MEDIUM.toString())
			.set(field(Task::getAssignedUserId), 1L)
			.set(field(Task::isDeleted), false)
			.set(field(Task::getDeletedAt), null)
			.create();

		taskEntity = Instancio.of(TaskEntity.class)
			.set(field(TaskEntity::getUid), task.getUid())
			.set(field(TaskEntity::getName), task.getName())
			.set(field(TaskEntity::getStatus), TaskStatus.PENDING)
			.set(field(TaskEntity::getPriority), PriorityStatus.MEDIUM)
			.set(field(TaskEntity::getAssignedUserId), task.getAssignedUserId())
			.set(field(TaskEntity::isDeleted), false)
			.set(field(TaskEntity::getDeletedAt), null)
			.set(field(TaskEntity::getProject), projectEntity)
			.create();

		pageable = PageRequest.of(0, 10);
	}

	@Test
	void save() {
		when(taskPersistenceMapper.toEntity(task)).thenReturn(taskEntity);
		when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
		when(taskPersistenceMapper.toDomain(taskEntity)).thenReturn(task);

		Task result = taskPersistenceAdapter.save(task);

		assertThat(result).isEqualTo(task);
		verify(taskPersistenceMapper).toEntity(task);
		verify(taskRepository).save(taskEntity);
		verify(taskPersistenceMapper).toDomain(taskEntity);
	}

	@Test
	void searchAll() {
		when(taskRepository.searchAll(pageable, task.getAssignedUserId())).thenReturn(List.of(taskEntity));
		when(taskPersistenceMapper.toTasksList(List.of(taskEntity))).thenReturn(List.of(task));

		List<Task> result = taskPersistenceAdapter.searchAll(pageable, task.getAssignedUserId());

		assertThat(result).isEqualTo(List.of(task));
		verify(taskRepository).searchAll(pageable, task.getAssignedUserId());
		verify(taskPersistenceMapper).toTasksList(List.of(taskEntity));
	}

	@Test
	void findByUid() {
		when(taskRepository.findByUid(task.getUid())).thenReturn(taskEntity);
		when(taskPersistenceMapper.toDomain(taskEntity)).thenReturn(task);

		Task result = taskPersistenceAdapter.findByUid(task.getUid());

		assertThat(result).isEqualTo(task);
		verify(taskRepository).findByUid(task.getUid());
		verify(taskPersistenceMapper).toDomain(taskEntity);
	}

	@Test
	void findByProjectUid() {
		when(taskRepository.findByProjectUid(pageable, projectEntity.getUid())).thenReturn(List.of(taskEntity));
		when(taskPersistenceMapper.toTasksList(List.of(taskEntity))).thenReturn(List.of(task));

		List<Task> result = taskPersistenceAdapter.findByProjectUid(pageable, projectEntity.getUid());

		assertThat(result).isEqualTo(List.of(task));
		verify(taskRepository).findByProjectUid(pageable, projectEntity.getUid());
		verify(taskPersistenceMapper).toTasksList(List.of(taskEntity));
	}

	@Test
	void findByAssignedUserId() {
		when(taskRepository.findByAssignedUserId(pageable, task.getAssignedUserId())).thenReturn(List.of(taskEntity));
		when(taskPersistenceMapper.toTasksList(List.of(taskEntity))).thenReturn(List.of(task));

		List<Task> result = taskPersistenceAdapter.findByAssignedUserId(pageable, task.getAssignedUserId());

		assertThat(result).isEqualTo(List.of(task));
		verify(taskRepository).findByAssignedUserId(pageable, task.getAssignedUserId());
		verify(taskPersistenceMapper).toTasksList(List.of(taskEntity));
	}

	@Test
	void findByProjectUidAndAssignedUserIdPaginated() {
		when(taskRepository.findByProjectUidAndAssignedUserIdPaginated(projectEntity.getUid(), task.getAssignedUserId(), pageable))
			.thenReturn(List.of(taskEntity));
		when(taskPersistenceMapper.toTasksList(List.of(taskEntity))).thenReturn(List.of(task));

		List<Task> result = taskPersistenceAdapter.findByProjectUidAndAssignedUserIdPaginated(
			projectEntity.getUid(), task.getAssignedUserId(), pageable
		);

		assertThat(result).isEqualTo(List.of(task));
		verify(taskRepository).findByProjectUidAndAssignedUserIdPaginated(projectEntity.getUid(), task.getAssignedUserId(), pageable);
		verify(taskPersistenceMapper).toTasksList(List.of(taskEntity));
	}

	@Test
	void findByProjectUidAndAssignedUserId() {
		when(taskRepository.findByProjectUidAndAssignedUserId(projectEntity.getUid(), task.getAssignedUserId()))
			.thenReturn(List.of(taskEntity));
		when(taskPersistenceMapper.toTasksList(List.of(taskEntity))).thenReturn(List.of(task));

		List<Task> result = taskPersistenceAdapter.findByProjectUidAndAssignedUserId(projectEntity.getUid(), task.getAssignedUserId());

		assertThat(result).isEqualTo(List.of(task));
		verify(taskRepository).findByProjectUidAndAssignedUserId(projectEntity.getUid(), task.getAssignedUserId());
		verify(taskPersistenceMapper).toTasksList(List.of(taskEntity));
	}
}