package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence;

import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.ProjectUserPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectUserEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.ProjectUserRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectUserPersistenceAdapter implements ProjectUserPersistencePort {

	private final ProjectUserRepository projectUserRepository;

	private final ProjectUserPersistenceMapper projectUserPersistenceMapper;

	@Transactional
	@Override
	public ProjectUser save(ProjectUser projectUser) {
		ProjectUserEntity projectUserEntity = projectUserPersistenceMapper.toEntity(projectUser);
		ProjectUserEntity savedProjectUserEntity = projectUserRepository.save(projectUserEntity);
		return projectUserPersistenceMapper.toDomain(savedProjectUserEntity);
	}

	@Override
	public List<ProjectUser> saveAll(List<ProjectUser> projectUsers) {
		List<ProjectUserEntity> projectUserEntities = projectUserPersistenceMapper.toEntityList(projectUsers);
		List<ProjectUserEntity> savedProjectUserEntities = projectUserRepository.saveAll(projectUserEntities);
		return projectUserPersistenceMapper.toProjectUserList(savedProjectUserEntities);
	}

	@Transactional(readOnly = true)
	@Override
	public ProjectUser findByProjectUidAndUserId(String projectUid, Long userId) {
		ProjectUserEntity projectUserEntity = projectUserRepository.findByProjectUidAndUserId(projectUid, userId);
		return projectUserPersistenceMapper.toDomain(projectUserEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProjectUser> findByProjectUid(String projectUid) {
		List<ProjectUserEntity> projectUserEntity = projectUserRepository.findByProjectUid(projectUid);
		return projectUserPersistenceMapper.toProjectUserList(projectUserEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProjectUser> findByUserId(Long userId) {
		List<ProjectUserEntity> projectUserEntity = projectUserRepository.findByUserId(userId);
		return projectUserPersistenceMapper.toProjectUserList(projectUserEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public ProjectUser findByUid(String uid) {
		return projectUserPersistenceMapper.toDomain(projectUserRepository.findByUid(uid));
	}

	@Override
	public boolean existsByProjectUidAndUserId(String projectUid, Long userId) {
		return projectUserRepository.existsByProjectUidAndUserId(projectUid, userId);
	}
}
