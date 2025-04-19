package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectUserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectUserMapper {

	ProjectUserResponseDto toResponse(ProjectUser projectUser);

	List<ProjectUserResponseDto> toResponseList(List<ProjectUser> projectUsers);
}
