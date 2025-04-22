package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.ui;

import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.OpenApiResponses;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.ValidateUsersDto;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
@Tag(name = "Project Management", description = "Endpoints for managing projects")
public interface ProjectRestUI {

	@PostMapping
	@ApiResponse(responseCode = "201", description = "Project created successfully")
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
	ResponseEntity<ProjectResponseDto> create(@Valid @RequestBody CreateProjectDto createProjectDto);

	@GetMapping("/search")
	@ApiResponse(responseCode = "200", description = "Projects found successfully")
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
	ResponseEntity<PaginatedResponseDto> search(@Valid @ModelAttribute SearchParamsDto params);

	@GetMapping("/{uid}")
	@ApiResponse(responseCode = "200", description = "Project found successfully")
	@ApiResponse(responseCode = "404", description = "Project not found",
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
	ResponseEntity<ProjectResponseDto> findProject(@PathVariable String uid);

	@PatchMapping("/{uid}")
	@ApiResponse(responseCode = "200", description = "Project updated successfully")
	@ApiResponse(responseCode = "404", description = "Project not found",
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
	ResponseEntity<ProjectResponseDto> updateProject(@PathVariable String uid, @Valid @RequestBody PatchProjectDto patchProjectDto);

	@DeleteMapping("/{uid}")
	@ApiResponse(responseCode = "200", description = "Project deleted successfully",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponseDto.class)))
	@ApiResponse(responseCode = "404", description = "Project not found",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
	@ApiResponse(
		responseCode = "401",
		description = "Unauthorized",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
			)))
	ResponseEntity<DeleteResponseDto> removeProject(@PathVariable String uid);


	@PostMapping("/{projectUid}/members")
	@ApiResponse(responseCode = "200", description = "Members added successfully")
	@ApiResponse(responseCode = "404", description = "Project not found",
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
	ResponseEntity<ProjectResponseDto> addMembersToProject(@PathVariable String projectUid, @Valid @RequestBody ValidateUsersDto request);

	@DeleteMapping("/{projectUid}/members/{memberId}")
	@ApiResponse(responseCode = "200", description = "Member removed successfully",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = ProjectResponseDto.class)))
	@ApiResponse(responseCode = "404", description = "Project or member not found",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
	@ApiResponse(responseCode = "400", description = "Bad request",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.BAD_REQUEST_EXAMPLE)))
	@ApiResponse(responseCode = "401", description = "Unauthorized",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE)))
	ResponseEntity<ProjectResponseDto> removeMemberFromProject(@PathVariable String projectUid, @PathVariable Long memberId);
}
