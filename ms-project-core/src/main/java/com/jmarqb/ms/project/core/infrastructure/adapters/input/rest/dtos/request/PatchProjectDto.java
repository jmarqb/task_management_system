package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request;

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
@Schema(description = "DTO for partially updating a project")
public class PatchProjectDto {

	@JsonIgnore
	@Schema(hidden = true)
	private String uid;

	@Schema(description = "Optional name update", example = "Updated T-shirt")
	@Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
	private String name;

	@Schema(description = "Optional description update", example = "Updated T-shirt Description")
	@Size(min = 3, max = 50, message = "description must be between 3 and 50 characters")
	private String description;

	@Schema(description = "Optional archived update", example = "true")
	private boolean archived;
}
