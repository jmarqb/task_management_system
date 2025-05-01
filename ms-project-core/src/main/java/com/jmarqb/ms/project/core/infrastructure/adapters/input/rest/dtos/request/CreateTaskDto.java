package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request;

import com.jmarqb.ms.project.core.application.vo.PriorityStatus;
import com.jmarqb.ms.project.core.application.vo.TaskStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Schema(description = "DTO for creating a new task.")
public class CreateTaskDto {

	@Schema(description = "name must be between 3 and 30 characters", example = "advent-code")
	@NotBlank
	@Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
	private String name;

	@Schema(description = "projectId must be a valid UUID", example = "7b2f4f93-c450-4f2f-9c6f-d3c2f39f9a8c")
	@NotBlank
	@UUID(message = "projectId must be a valid UUID")
	private String projectId;

	@Schema(description = "assignedUserId must be a valid Long", example = "1")
	@Min(value = 1, message = "assignedUserId must be a valid Long")
	@NotNull(message = "assignedUserId is required")
	private Long assignedUserId;

	@Schema(hidden = true, defaultValue = "PENDING")
	private String status = TaskStatus.PENDING.toString();

	@Schema(hidden = true, defaultValue = "MEDIUM")
	private String priority = PriorityStatus.MEDIUM.toString();
}
