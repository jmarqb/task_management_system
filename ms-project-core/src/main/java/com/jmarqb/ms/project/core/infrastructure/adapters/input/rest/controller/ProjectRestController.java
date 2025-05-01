package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import com.jmarqb.ms.project.core.application.ports.input.ProjectUseCase;
import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.ports.output.external.User;
import com.jmarqb.ms.project.core.domain.ports.output.external.UserClient;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper.ProjectMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper.ProjectUserMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper.TaskMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.ui.ProjectRestUI;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.ValidateUsersDto;
import com.jmarqb.ms.project.core.infrastructure.security.CustomAuthenticationDetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectRestController implements ProjectRestUI {

	private final ProjectUseCase projectUseCase;

	private final ProjectMapper projectMapper;

	private final ProjectUserMapper projectUserMapper;

	private final TaskMapper taskMapper;

	private final UserClient userClient;

	@Override
	public ResponseEntity<ProjectResponseDto> create(CreateProjectDto createProjectDto) {
		createProjectDto.setOwnerId(this.getAuthenticationDetails().claims().get("id", Long.class));
		Project project = projectUseCase.save(projectMapper.toDomain(createProjectDto));
		return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toResponse(project));
	}

	@Override
	public ResponseEntity<PaginatedResponseDto> search(SearchParamsDto params) {
		params.setUserId(this.getAuthenticationDetails().claims().get("id", Long.class));

		List<Project> projectsList = projectUseCase.searchAll(params.getPage(), params.getSize(),
			params.getSort(), params.getUserId());

		List<ProjectResponseDto> response = projectMapper.toResponseListFormatted(projectsList,
			projectUserMapper, taskMapper);

		return ResponseEntity.status(HttpStatus.OK).body(projectMapper.toPaginatedResponse(response, projectsList.size()
		, params.getPage(), params.getSize(), LocalDateTime.now()));
	}

	@Override
	public ResponseEntity<ProjectResponseDto> findProject(String uid) {
		Project project = projectUseCase.findProjectByUid(uid);
		ProjectResponseDto response = projectMapper.toResponseFormatted(project, projectUserMapper, taskMapper);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	public ResponseEntity<ProjectResponseDto> updateProject(String uid, PatchProjectDto patchProjectDto) {
		patchProjectDto.setUid(uid);
		Project project = projectUseCase.updateProject(projectMapper.toDomain(patchProjectDto), this.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(projectMapper.toResponse(project));
	}

	@Override
	public ResponseEntity<DeleteResponseDto> removeProject(String uid) {
		projectUseCase.deleteProject(uid, this.getUserId());
		return new ResponseEntity<>(new DeleteResponseDto(true, 1), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ProjectResponseDto> addMembersToProject(String projectUid, ValidateUsersDto request) {
		try {
			List<User> userList = userClient.checkUsers(request.getUsersIds());

			List<Long> userIds = userList.stream().map(User::id).toList();
			Project project = projectUseCase.addMembersToProject(projectUid, userIds, this.getUserId());
			ProjectResponseDto response = projectMapper.toResponseFormatted(project,
				projectUserMapper, taskMapper);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public ResponseEntity<ProjectResponseDto> removeMemberFromProject(String projectUid, Long memberId) {
		try {
			Project project = projectUseCase.removeMemberFromProject(projectUid, memberId, this.getUserId());
			return ResponseEntity.status(HttpStatus.OK).body(projectMapper.toResponseFormatted(project,
				projectUserMapper, taskMapper));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	private CustomAuthenticationDetails getAuthenticationDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (CustomAuthenticationDetails) authentication.getDetails();
	}

	private Long getUserId() {
		return this.getAuthenticationDetails().claims().get("id", Long.class);
	}
}
