package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.TaskPersistencePort;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.TaskPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.TaskEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.TaskRepository;

@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements TaskPersistencePort {

	private final TaskRepository taskRepository;

	private final TaskPersistenceMapper taskPersistenceMapper;

	@Transactional
	@Override
	public Task save(Task task) {
		TaskEntity taskEntity = taskPersistenceMapper.toEntity(task);
		TaskEntity savedTaskEntity = taskRepository.save(taskEntity);
		return taskPersistenceMapper.toDomain(savedTaskEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Task> searchAll(Pageable pageable, Long userId) {
		return this.taskPersistenceMapper.toTasksList(taskRepository.searchAll(pageable, userId));
	}

	@Transactional(readOnly = true)
	@Override
	public Task findByUid(String uid) {
		return taskPersistenceMapper.toDomain(taskRepository.findByUid(uid));
	}

	@Transactional(readOnly = true)
	@Override
	public List<Task> findByProjectUid(Pageable pageable, String projectUid) {
		List<TaskEntity> taskEntities = taskRepository.findByProjectUid(pageable, projectUid);
		return taskPersistenceMapper.toTasksList(taskEntities);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Task> findByAssignedUserId(Pageable pageable, Long userId) {
		List<TaskEntity> taskEntities = taskRepository.findByAssignedUserId(pageable, userId);
		return taskPersistenceMapper.toTasksList(taskEntities);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Task> findByProjectUidAndAssignedUserIdPaginated(String projectUid, Long userId, Pageable pageable) {
		List<TaskEntity> taskEntities = taskRepository.findByProjectUidAndAssignedUserIdPaginated(projectUid, userId, pageable);
		return taskPersistenceMapper.toTasksList(taskEntities);
	}

	@Override
	public List<Task> findByProjectUidAndAssignedUserId(String projectUid, Long userId) {
		List<TaskEntity> taskEntities = taskRepository.findByProjectUidAndAssignedUserId(projectUid, userId);
		return taskPersistenceMapper.toTasksList(taskEntities);
	}

}
