package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper;

import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectTask;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.TaskResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

	@Mapping(target = "assignedUserId", source = "assignedUserId")
	@Mapping(target = "project.uid", source = "projectId")
	Task toDomain(CreateTaskDto createTaskDto);

	Task toDomain(PatchTaskDto patchTaskDto);

	@Mapping(target = "projectId", source = "project.uid")
	TaskResponseDto toResponseD(Task task);

	@Mapping(target = "projectId", source = "projId")
	TaskResponseDto toResponse(Task task, String projId);

	List<ProjectTask> toResponseListPartialTask(List<Task> tasks);

	@Mapping(target = "data", source = "tasks")
	@Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
	PaginatedResponseDto toPaginatedResponse(List<?> tasks, int total, int page, int size, LocalDateTime timestamp);
}
