package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.jmarqb.ms.project.core.application.exceptions.ProjectNotFoundException;
import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectPersistencePort;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.ProjectPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.ProjectUserPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.TaskPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.ProjectRepository;

@Component
@RequiredArgsConstructor
public class ProjectPersistenceAdapter implements ProjectPersistencePort {

	private final ProjectRepository projectRepository;

	private final ProjectPersistenceMapper projectPersistenceMapper;
	private final ProjectUserPersistenceMapper projectUserPersistenceMapper;
	private final TaskPersistenceMapper taskPersistenceMapper;

	@Transactional
	@Override
	public Project save(Project project) {
		ProjectEntity projectEntity = this.projectPersistenceMapper.toEntity(project);
		ProjectEntity savedProjectEntity = this.projectRepository.save(projectEntity);
		return this.projectPersistenceMapper.toDomainWithMembers(savedProjectEntity, projectUserPersistenceMapper, taskPersistenceMapper);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Project> searchAll(Pageable pageable, Long userId) {
		return this.projectPersistenceMapper.toProjectsListWithMembers(
			this.projectRepository.searchAll(pageable, userId), projectUserPersistenceMapper, taskPersistenceMapper);
	}

	@Transactional(readOnly = true)
	@Override
	public Project findByUid(String uid) {
		ProjectEntity projectEntity = this.projectRepository.findByUid(uid);
		if (projectEntity == null) {
			throw new ProjectNotFoundException("Project with id %s not found".formatted(uid));
		}
		return this.projectPersistenceMapper.toDomainWithMembers(projectEntity, projectUserPersistenceMapper, taskPersistenceMapper);
	}
}
