package com.jmarqb.ms.project.core.domain.ports.output.persistence;

import com.jmarqb.ms.project.core.domain.model.Pagination;
import com.jmarqb.ms.project.core.domain.model.Task;

import java.util.List;

public interface TaskPersistencePort {
	Task save(Task task);

	List<Task> searchAll(Pagination pagination, Long userId);

	Task findByUid(String uid);

	List<Task> findByProjectUid(Pagination pagination, String projectUid);

	List<Task> findByAssignedUserId(Pagination pagination, Long userId);

	List<Task> findByProjectUidAndAssignedUserIdPaginated(String projectUid, Long userId, Pagination pagination);

	List<Task> findByProjectUidAndAssignedUserId(String projectUid, Long userId);

}
