package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, String> {

	TaskEntity save(Task task);

	@Query("SELECT r FROM TaskEntity r WHERE r.deleted = false AND r.project.ownerId = :userId OR r.assignedUserId = :userId")
	List<TaskEntity> searchAll(Pageable pageable, Long userId);

	@Query("SELECT r FROM TaskEntity r WHERE r.deleted = false AND r.uid = :uid")
	TaskEntity findByUid(String uid);

	@Query("SELECT r FROM TaskEntity r WHERE r.deleted = false AND r.project.uid = :projectUid")
	List<TaskEntity> findByProjectUid(Pageable pageable, String projectUid);

	@Query("SELECT r FROM TaskEntity r WHERE r.deleted = false AND r.assignedUserId = :userId")
	List<TaskEntity> findByAssignedUserId(Pageable pageable, Long userId);

	@Query("SELECT r FROM TaskEntity r WHERE r.deleted = false AND r.project.uid = :projectUid AND r.assignedUserId = :userId")
	List<TaskEntity> findByProjectUidAndAssignedUserIdPaginated(String projectUid, Long userId, Pageable pageable);

	@Query("SELECT r FROM TaskEntity r WHERE r.deleted = false AND r.project.uid = :projectUid AND r.assignedUserId = :userId")
	List<TaskEntity> findByProjectUidAndAssignedUserId(String projectUid, Long userId);


}
