package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectPersistenceMapper {

	@Mapping(target = "archived", source = "archived")
	ProjectEntity toEntity(Project project);

	@Mapping(target = "members", ignore = true)
	@Mapping(target = "tasks", ignore = true)
	Project toDomain(ProjectEntity projectEntity);

	@Mapping(target = "isArchived", source = "archived")
	List<Project> toProjectsList(List<ProjectEntity> projectEntities);

	List<ProjectEntity> toProjectEntityList(List<Project> projects);

	default Project toDomainWithMembers(ProjectEntity projectEntity,
																			ProjectUserPersistenceMapper projectUserPersistenceMapper,
																			TaskPersistenceMapper taskPersistenceMapper) {
		Project project = toDomain(projectEntity);
		if (projectEntity.getMembers() != null) {
			project.setMembers(projectUserPersistenceMapper.toProjectUserList(
				projectEntity.getMembers()));
		}
		if (projectEntity.getTasks() != null) {
			project.setTasks(taskPersistenceMapper.toTasksList(projectEntity.getTasks()));
		}
		return project;
	}

	default List<Project> toProjectsListWithMembers(List<ProjectEntity> projectEntities,
																									ProjectUserPersistenceMapper projectUserPersistenceMapper,
																									TaskPersistenceMapper taskPersistenceMapper) {
		List<Project> projects = new ArrayList<>();
		for (ProjectEntity projectEntity : projectEntities) {
			projects.add(toDomainWithMembers(projectEntity, projectUserPersistenceMapper, taskPersistenceMapper));
		}
		return projects;
	}
}
