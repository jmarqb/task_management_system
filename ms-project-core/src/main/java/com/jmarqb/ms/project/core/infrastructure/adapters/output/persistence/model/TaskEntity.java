package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model;

import com.jmarqb.ms.project.core.application.vo.PriorityStatus;
import com.jmarqb.ms.project.core.application.vo.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tasks", indexes = {
	@Index(name = "idx_task_name", columnList = "name"),
	@Index(name = "idx_task_uid", columnList = "uid"),
	@Index(name = "idx_task_project_id", columnList = "project_id"),
	@Index(name = "idx_task_assigned_user", columnList = "assigned_user_id"),
	@Index(name = "idx_task_deleted", columnList = "deleted")
})
public class TaskEntity {

	@Id
	@Column(name = "uid", nullable = false, updatable = false)
	private String uid;

	@Column(name = "name", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private TaskStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "priority", nullable = false)
	private PriorityStatus priority;

	@ToString.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private ProjectEntity project;

	@Column(name = "assigned_user_id")
	private Long assignedUserId;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}

