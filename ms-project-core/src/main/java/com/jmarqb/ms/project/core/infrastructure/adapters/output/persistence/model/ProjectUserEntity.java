package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model;

import com.jmarqb.ms.project.core.application.vo.ProjectUserRole;

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
@Table(name = "project_users", indexes = {
	@Index(name = "idx_pu_project_uid", columnList = "project_id"),
	@Index(name = "idx_pu_user_id", columnList = "user_id"),
	@Index(name = "idx_pu_deleted", columnList = "deleted")
})
public class ProjectUserEntity {

	@Id
	@Column(name = "uid", nullable = false, updatable = false)
	private String uid;

	@ToString.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private ProjectEntity project;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private ProjectUserRole role;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}

