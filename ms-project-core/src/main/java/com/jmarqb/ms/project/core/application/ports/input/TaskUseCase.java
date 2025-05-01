package com.jmarqb.ms.project.core.application.ports.input;

import com.jmarqb.ms.project.core.domain.model.Task;

import java.util.List;

public interface TaskUseCase {

	Task save(Task task, Long userId);

	Task findTaskByUid(String uid);

	List<Task> searchAll(int page, int size, String sort, Long userId);

	List<Task> searchAllByProjectUid(String projectUid, int page, int size, String sort);

	List<Task> searchAllByAssignedUserId(Long userId, int page, int size, String sort);

	List<Task> searchAllByProjectUidAndAssignedUserId(String projectUid, Long userId, int page, int size, String sort);

	Task updateTask(Task task, Long userId);

	void deleteTask(String uid, Long userId);

	void filterTaskUser(Task task, Long userId);

}
