package com.ms.auth.infrastructure.adapters.input.rest.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateRoleResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
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
@Tag(name = "Role Management", description = "Endpoints for managing roles")
public interface RoleRestUI {

	@PostMapping
	@ApiResponse(responseCode = "201", description = "Role created successfully")
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
	ResponseEntity<CreateRoleResponseDto> create(@Valid @RequestBody CreateRoleDto createRoleDto);


	@PostMapping("/search")
	@ApiResponse(responseCode = "200", description = "Roles found successfully")
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
	ResponseEntity<PaginatedResponseDto> search(@Valid @RequestBody SearchBodyDto searchBodyDto);

	@GetMapping("/{id}")
	@ApiResponse(responseCode = "200", description = "Role found successfully")
	@ApiResponse(responseCode = "404", description = "Role not found",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
	@ApiResponse(
		responseCode = "401",
		description = "Unauthorized",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
			)))
	ResponseEntity<CreateRoleResponseDto> findRole(@PathVariable Long id);


	@PatchMapping("/{id}")
	@ApiResponse(responseCode = "200", description = "Role updated successfully")
	@ApiResponse(responseCode = "404", description = "Role not found",
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
	ResponseEntity<CreateRoleResponseDto> updateRole(@PathVariable Long id, @RequestBody UpdateRoleDto updateRoleDto);


	@DeleteMapping("/{id}")
	@ApiResponse(responseCode = "200", description = "Role deleted successfully",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponseDto.class)))
	@ApiResponse(responseCode = "404", description = "Role not found",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
	@ApiResponse(
		responseCode = "401",
		description = "Unauthorized",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
			)))
	ResponseEntity<DeleteResponseDto> removeRole(@PathVariable Long id);
}
