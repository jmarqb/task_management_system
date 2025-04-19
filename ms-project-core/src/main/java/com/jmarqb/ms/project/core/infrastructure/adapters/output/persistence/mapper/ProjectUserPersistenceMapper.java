package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectUserEntity;
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
