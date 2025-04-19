package com.jmarqb.ms.project.core.domain.ports.output.persistence;

import org.springframework.data.domain.Pageable;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Task;

public interface TaskPersistencePort {
	Task save(Task task);

	List<Task> searchAll(Pageable pageable, Long userId);

	Task findByUid(String uid);

	List<Task> findByProjectUid(Pageable pageable, String projectUid);

	List<Task> findByAssignedUserId(Pageable pageable, Long userId);

	List<Task> findByProjectUidAndAssignedUserIdPaginated(String projectUid, Long userId, Pageable pageable);

	List<Task> findByProjectUidAndAssignedUserId(String projectUid, Long userId);

}
