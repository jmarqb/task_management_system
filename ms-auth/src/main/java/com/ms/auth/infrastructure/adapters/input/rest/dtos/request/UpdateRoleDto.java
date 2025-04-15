package com.ms.auth.infrastructure.adapters.input.rest.dtos.request;

import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
public class UpdateRoleDto {

	@JsonIgnore
	@Schema(hidden = true)
	private Long id;

	@Schema(description = "name must be between 3 and 30 characters", example = "ROLE_NAME")
	@Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
	private String name;

	@Schema(description = "description must be between 3 and 30 characters", example = "ROLE_DESCRIPTION")
	@Size(min = 3, max = 30, message = "description must be between 3 and 30 characters")
	private String description;

	@Schema(description = "icon", example = "ROLE_ICON")
	private String icon;

	@Schema(description = "isAdmin", example = "false")
	private Boolean isAdmin;

	@Schema(description = "isDefaultRole", example = "true")
	private Boolean isDefaultRole;

}
