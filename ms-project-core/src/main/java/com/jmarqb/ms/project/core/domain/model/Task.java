package com.jmarqb.ms.project.core.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
	private String uid;

	private String name;

	private String status;

	private String priority;

	private Project project;

	private Long assignedUserId;

	private boolean deleted;

	private LocalDateTime deletedAt;
}
