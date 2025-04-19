package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response;

import java.util.Date;
import java.util.List;

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
@Schema(description = "ProjectResponseDto")
public class ProjectResponseDto {

	@Schema(description = "project uid", example = "7b2f4f93-c450-4f2f-9c6f-d3c2f39f9a8c")
	private String uid;

	@Schema(description = "name", example = "Project Name")
	private String name;

	@Schema(description = "description", example = "Project Description")
	private String description;

	@Schema(description = "isArchived", example = "false")
	private boolean isArchived;

	@Schema(description = "ownerId", example = "1")
	private Long ownerId;

	@Schema(description = "deleted", example = "false")
	private boolean deleted;

	@Schema(description = "deletedAt", example = "null")
	private Date deletedAt;

	private List<ProjectUserResponseDto> members;

	private List<ProjectTask> tasks;
}
