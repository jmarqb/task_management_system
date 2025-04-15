package com.ms.auth.infrastructure.adapters.input.rest.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleToUsersDto {

	@Schema(description = "usersId must be at least 1, its an array", example = "[1,2,3]")
	@Size(min = 1, message = "usersId must be at least 1")
	@NotNull(message = "usersId is required")
	private Long[] usersId;

	@Schema(description = "roleId must be at least 1", example = "1")
	@NotNull(message = "roleId is required")
	@Min(value = 1, message = "roleId must be at least 1")
	private Long roleId;
}
