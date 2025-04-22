package com.jmarqb.ms.project.core.application.mapper;

import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.model.Task;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UpdateFieldMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateProject(Project source, @MappingTarget Project target);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateTask(Task source, @MappingTarget Task target);
}
