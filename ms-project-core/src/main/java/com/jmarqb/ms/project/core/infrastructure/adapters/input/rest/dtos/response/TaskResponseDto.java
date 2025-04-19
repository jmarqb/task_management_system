package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "TaskResponseDto")
public class TaskResponseDto {

	@Schema(description = "task uid", example = "7b2f4f93-c450-4f2f-9c6f-d3c2f39f9a8c")
	private String uid;

	@Schema(description = "name", example = "task Name")
	private String name;

	@Schema(description = "status", example = "PENDING")
	private String status;

	@Schema(description = "priority", example = "HIGH")
	private String priority;

	@Schema(description = "project id", example = "7b2f4f93-c450-4f2f-9c6f-d3c2f39f9a8c")
	private String projectId;

	@Schema(description = "assigned user id", example = "1")
	private Long assignedUserId;

	@Schema(description = "deleted", example = "false")
	private boolean deleted;

	@Schema(description = "deletedAt", example = "null")
	private Date deletedAt;
}
