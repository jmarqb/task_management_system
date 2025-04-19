package com.jmarqb.ms.project.core.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

	private String uid;

	private String name;

	private String description;

	private boolean isArchived;

	private Long ownerId;

	private List<ProjectUser> members;

	private List<Task> tasks;

	private boolean deleted;

	private LocalDateTime deletedAt;
}
