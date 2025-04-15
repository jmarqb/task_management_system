package com.ms.auth.infrastructure.adapters.input.rest.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.RoleToUsersDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.OpenApiResponses;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/roles")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Roles Users")
public interface RoleUserRestUI {

	@PostMapping("/add/to-many-users")
	@ApiResponse(responseCode = "200", description = "Role added to many users successfully")
	@ApiResponse(responseCode = "404", description = "Role not found or user not found",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
	@ApiResponse(responseCode = "400", description = "Bad request",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.BAD_REQUEST_EXAMPLE)))
	@ApiResponse(
		responseCode = "401",
		description = "Unauthorized",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
			)))
	ResponseEntity<PaginatedResponseDto> addRoleToManyUsers(@Valid @RequestBody RoleToUsersDto roleToUsersDto);


	@DeleteMapping("/remove/to-many-users")
	@ApiResponse(responseCode = "200", description = "Role added to many users successfully")
	@ApiResponse(responseCode = "404", description = "Role not found or user not found",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
	@ApiResponse(responseCode = "400", description = "Bad request",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.BAD_REQUEST_EXAMPLE)))
	@ApiResponse(
		responseCode = "401",
		description = "Unauthorized",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
			)))
	ResponseEntity<PaginatedResponseDto> removeRoleToManyUsers(@Valid @RequestBody RoleToUsersDto roleToUsersDto);
}
