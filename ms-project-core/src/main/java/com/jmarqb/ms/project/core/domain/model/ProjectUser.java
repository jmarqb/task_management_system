package com.jmarqb.ms.project.core.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProjectUser {

	private String uid;

	private Project project;

	private Long userId;

	private String role;

	private boolean deleted;

	private LocalDateTime deletedAt;
}
