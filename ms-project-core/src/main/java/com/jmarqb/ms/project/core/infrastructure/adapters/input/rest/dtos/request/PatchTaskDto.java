package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request;

import com.jmarqb.ms.project.core.application.vo.PriorityStatus;
import com.jmarqb.ms.project.core.application.vo.TaskStatus;
import com.jmarqb.ms.project.core.infrastructure.adapters.validation.ValueOfEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "DTO for partially updating a task")
public class PatchTaskDto {

	@JsonIgnore
	@Schema(hidden = true)
	private String uid;

	@Schema(description = "Optional name update", example = "Updated T-shirt")
	@Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
	private String name;

	@Schema(description = "assignedUserId must be a valid Long", example = "1")
	@Min(value = 1, message = "assignedUserId must be a valid Long")
	@NotNull(message = "assignedUserId is required")
	private Long assignedUserId;

	@Schema(description = "status must be [PENDING|IN_PROGRESS|DONE]", example = "PENDING")
	@NotBlank
	@ValueOfEnum(enumClass = TaskStatus.class, message = "status must be [PENDING|IN_PROGRESS|DONE]")
	private String status;

	@Schema(description = "priority must be [LOW|MEDIUM|HIGH]", example = "LOW")
	@NotBlank
	@ValueOfEnum(enumClass = PriorityStatus.class, message = "priority must be [LOW|MEDIUM|HIGH]")
	private String priority;
}
