package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.ports.output.external.User;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProjectUserMapper.class, TaskMapper.class})
public interface ProjectMapper {


	Project toDomain(CreateProjectDto createProjectDto);

	@Mapping(target = "members", source = "userList")
	Project toDomain(CreateProjectDto createProjectDto, List<User> userList);

	Project toDomain(PatchProjectDto patchProjectDto);

	@Mapping(target = "uid", source = "project.uid")
	@Mapping(target = "members", source = "project.members")
	@Mapping(target = "tasks", source = "project.tasks")
	ProjectResponseDto toResponse(Project project);


	@Mapping(target = "data", source = "projects")
	@Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
	PaginatedResponseDto toPaginatedResponse(List<?> projects, int total, int page, int size, LocalDateTime timestamp);

	default ProjectResponseDto toResponseFormatted(Project project, ProjectUserMapper projectUserMapper,
																								TaskMapper taskMapper) {
		ProjectResponseDto response = toResponse(project);
		if (project.getMembers() != null) {
			response.setMembers(projectUserMapper.toResponseList(project.getMembers()));
		}
		if (project.getTasks() != null) {
			response.setTasks(taskMapper.toResponseListPartialTask(project.getTasks()));
		}
		return response;
	}

	default List<ProjectResponseDto> toResponseListFormatted(List<Project> projects, ProjectUserMapper projectUserMapper,
																													TaskMapper taskMapper) {
		List<ProjectResponseDto> list = new ArrayList<>();
		for (Project project : projects) {
			list.add(toResponseFormatted(project, projectUserMapper, taskMapper));
		}
		return list;
	}
}
