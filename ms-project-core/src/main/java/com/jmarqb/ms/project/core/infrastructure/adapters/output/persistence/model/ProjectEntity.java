package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "projects", indexes = {
	@Index(name = "idx_proj_name", columnList = "name"),
	@Index(name = "idx_proj_uid", columnList = "uid"),
	@Index(name = "idx_proj_owner", columnList = "owner_id"),
	@Index(name = "idx_proj_archived", columnList = "archived"),
	@Index(name = "idx_proj_deleted", columnList = "deleted")
})
public class ProjectEntity {

	@Id
	@Column(name = "uid", nullable = false, updatable = false)
	private String uid;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "archived", nullable = false)
	private boolean archived;

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@ToString.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	private List<ProjectUserEntity> members;

	@ToString.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	private List<TaskEntity> tasks;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}

