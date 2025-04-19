package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskPersistenceMapper {

	TaskEntity toEntity(Task task);

	@Mapping(target = "project", expression = "java(mapProjectUidOnly(taskEntity.getProject()))")
	Task toDomain(TaskEntity taskEntity);

	List<Task> toTasksList(List<TaskEntity> taskEntities);

	default Project mapProjectUidOnly(ProjectEntity projectEntity) {
		if (projectEntity == null) return null;

		Project project = new Project();
		project.setUid(projectEntity.getUid());
		return project;
	}
}
