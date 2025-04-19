package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.jmarqb.ms.project.core.application.ports.input.TaskUseCase;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.TaskResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper.TaskMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.ui.TaskRestUI;
import com.jmarqb.ms.project.core.infrastructure.security.CustomAuthenticationDetails;

@Controller
@RequiredArgsConstructor
public class TaskRestController implements TaskRestUI {

	private final TaskUseCase taskUseCase;

	private final TaskMapper taskMapper;

	@Override
	public ResponseEntity<TaskResponseDto> create(CreateTaskDto createTaskDto) {
		Task task = taskMapper.toDomain(createTaskDto);
		Long userId = this.getUserId();
		Task returnedTask = taskUseCase.save(task, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(
			taskMapper.toResponse(returnedTask, createTaskDto.getProjectId()));
	}

	@Override
	public ResponseEntity<PaginatedResponseDto> search(SearchParamsDto params) {
		Long userId = this.getUserId();

		List<Task> tasksList = taskUseCase.searchAll(params.getPage(), params.getSize(), params.getSort(), userId);

		List<TaskResponseDto> response = tasksList.stream().map(taskMapper::toResponseD).toList();

		return ResponseEntity.status(HttpStatus.OK).body(taskMapper.toPaginatedResponse(response, tasksList.size(),
			params.getPage(), params.getSize(), LocalDateTime.now()));
	}

	@Override
	public ResponseEntity<TaskResponseDto> findTask(String uid) {
		Task task = taskUseCase.findTaskByUid(uid);
		taskUseCase.filterTaskUser(task, this.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(taskMapper.toResponse(task, task.getProject().getUid()));
	}

	@Override
	public ResponseEntity<TaskResponseDto> updateTask(String uid, PatchTaskDto patchTaskDto) {
		patchTaskDto.setUid(uid);
		Long userId = this.getUserId();

		Task task = taskUseCase.updateTask(taskMapper.toDomain(patchTaskDto), userId);
		return ResponseEntity.status(HttpStatus.OK).body(taskMapper.toResponse(task, task.getProject().getUid()));
	}

	@Override
	public ResponseEntity<DeleteResponseDto> removeTask(String uid) {
		taskUseCase.deleteTask(uid, this.getUserId());
		return new ResponseEntity<>(new DeleteResponseDto(true, 1), HttpStatus.OK);
	}

	private CustomAuthenticationDetails getAuthenticationDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (CustomAuthenticationDetails) authentication.getDetails();
	}

	private Long getUserId() {
		return this.getAuthenticationDetails().claims().get("id", Long.class);
	}
}
