package com.ms.auth.infrastructure.adapters.input.rest.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleDto {

	@Schema(description = "name must be between 3 and 30 characters", example = "ROLE_NAME")
	@NotBlank(message = "name is required")
	@Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
	private String name;

	@Schema(description = "description must be between 3 and 30 characters", example = "ROLE_DESCRIPTION")
	@NotBlank(message = "description is required")
	@Size(min = 3, max = 30, message = "description must be between 3 and 30 characters")
	private String description;

	@Schema(description = "icon", example = "ROLE_ICON")
	private String icon;

	@Schema(description = "isAdmin", example = "false")
	private Boolean isAdmin;

	@Schema(description = "isDefaultRole", example = "true")
	private Boolean isDefaultRole;

}
