package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectUserEntity;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectUserPersistenceMapper {

	ProjectUserEntity toEntity(ProjectUser projectUser);

	List<ProjectUserEntity> toEntityList(List<ProjectUser> projectUsers);

	@Mapping(target = "project", ignore = true)
	ProjectUser toDomain(ProjectUserEntity projectUserEntity);


	List<ProjectUser> toProjectUserList(List<ProjectUserEntity> projectUserEntities);
}
