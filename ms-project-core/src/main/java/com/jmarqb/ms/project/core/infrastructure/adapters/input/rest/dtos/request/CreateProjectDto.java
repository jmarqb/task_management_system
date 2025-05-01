package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request;

import jakarta.validation.constraints.NotBlank;
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
@Builder
@Getter
@Setter
@Schema(description = "DTO for creating a new project.")
public class CreateProjectDto {

	@Schema(description = "name must be between 3 and 30 characters", example = "advent-code")
	@NotBlank
	@Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
	private String name;

	@Schema(description = "description must be between 3 and 50 characters", example = "advent-code description")
	@NotBlank
	@Size(min = 3, max = 50, message = "description must be between 3 and 50 characters")
	private String description;

	@Schema(hidden = true)
	private Long ownerId;

	@JsonIgnore
	@Schema(hidden = true, defaultValue = "false")
	private boolean archived;
}
