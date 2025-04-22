package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectUserResponseDto;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectUserMapper {

	ProjectUserResponseDto toResponse(ProjectUser projectUser);

	List<ProjectUserResponseDto> toResponseList(List<ProjectUser> projectUsers);
}
