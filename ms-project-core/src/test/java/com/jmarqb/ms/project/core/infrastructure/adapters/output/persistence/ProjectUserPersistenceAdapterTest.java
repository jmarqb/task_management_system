package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence;

import com.jmarqb.ms.project.core.application.vo.ProjectUserRole;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.mapper.ProjectUserPersistenceMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectUserEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.ProjectUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectUserPersistenceAdapterTest {

	private @Mock ProjectUserRepository projectUserRepository;

	private @Mock ProjectUserPersistenceMapper projectUserPersistenceMapper;

	private @InjectMocks ProjectUserPersistenceAdapter projectUserPersistenceAdapter;

	private ProjectUser projectUser;

	private ProjectEntity projectEntity;
	private ProjectUserEntity projectUserEntity;

	@BeforeEach
	void setUp() {
		projectEntity = Instancio.of(ProjectEntity.class)
			.set(field(ProjectEntity::getUid), UUID.randomUUID().toString())
			.set(field(ProjectEntity::isDeleted), false)
			.set(field(ProjectEntity::getDeletedAt), null)
			.set(field(ProjectEntity::getMembers), new ArrayList<>())
			.set(field(ProjectEntity::getTasks), new ArrayList<>())
			.create();

		ProjectUserRole role = ProjectUserRole.MEMBER;

		projectUser = Instancio.of(ProjectUser.class)
			.set(field(ProjectUser::getUid), UUID.randomUUID().toString())
			.set(field(ProjectUser::getUserId), 1L)
			.set(field(ProjectUser::getRole), role.toString())
			.set(field(ProjectUser::isDeleted), false)
			.set(field(ProjectUser::getDeletedAt), null)
			.create();

		projectUserEntity = Instancio.of(ProjectUserEntity.class)
			.set(field(ProjectUserEntity::getUid), projectUser.getUid())
			.set(field(ProjectUserEntity::getUserId), projectUser.getUserId())
			.set(field(ProjectUserEntity::getRole), role)
			.set(field(ProjectUserEntity::isDeleted), false)
			.set(field(ProjectUserEntity::getDeletedAt), null)
			.set(field(ProjectUserEntity::getProject), projectEntity)
			.create();

	}

	@Test
	void save() {
		when(projectUserPersistenceMapper.toEntity(projectUser)).thenReturn(projectUserEntity);
		when(projectUserRepository.save(projectUserEntity)).thenReturn(projectUserEntity);
		when(projectUserPersistenceMapper.toDomain(projectUserEntity)).thenReturn(projectUser);

		ProjectUser result = projectUserPersistenceAdapter.save(projectUser);

		assertThat(result).isEqualTo(projectUser);
		verify(projectUserPersistenceMapper).toEntity(projectUser);
		verify(projectUserRepository).save(projectUserEntity);
		verify(projectUserPersistenceMapper).toDomain(projectUserEntity);
	}

	@Test
	void saveAll() {
		when(projectUserPersistenceMapper.toEntityList(List.of(projectUser))).thenReturn(List.of(projectUserEntity));
		when(projectUserRepository.saveAll(List.of(projectUserEntity))).thenReturn(List.of(projectUserEntity));
		when(projectUserPersistenceMapper.toProjectUserList(List.of(projectUserEntity))).thenReturn(List.of(projectUser));

		List<ProjectUser> result = projectUserPersistenceAdapter.saveAll(List.of(projectUser));

		assertThat(result).isEqualTo(List.of(projectUser));
		verify(projectUserPersistenceMapper).toEntityList(List.of(projectUser));
		verify(projectUserRepository).saveAll(List.of(projectUserEntity));
		verify(projectUserPersistenceMapper).toProjectUserList(List.of(projectUserEntity));
	}

	@Test
	void findByProjectUidAndUserId() {
		when(projectUserRepository.findByProjectUidAndUserId(projectEntity.getUid(), projectUser.getUserId()))
			.thenReturn(projectUserEntity);
		when(projectUserPersistenceMapper.toDomain(projectUserEntity)).thenReturn(projectUser);

		ProjectUser result = projectUserPersistenceAdapter.findByProjectUidAndUserId(projectEntity.getUid(), projectUser.getUserId());

		assertThat(result).isEqualTo(projectUser);
		verify(projectUserRepository).findByProjectUidAndUserId(projectEntity.getUid(), projectUser.getUserId());
		verify(projectUserPersistenceMapper).toDomain(projectUserEntity);
	}

	@Test
	void findByProjectUid() {
		when(projectUserRepository.findByProjectUid(projectEntity.getUid())).thenReturn(List.of(projectUserEntity));
		when(projectUserPersistenceMapper.toProjectUserList(List.of(projectUserEntity))).thenReturn(List.of(projectUser));

		List<ProjectUser> result = projectUserPersistenceAdapter.findByProjectUid(projectEntity.getUid());

		assertThat(result).isEqualTo(List.of(projectUser));
		verify(projectUserRepository).findByProjectUid(projectEntity.getUid());
		verify(projectUserPersistenceMapper).toProjectUserList(List.of(projectUserEntity));
	}

	@Test
	void findByUserId() {
		when(projectUserRepository.findByUserId(projectUser.getUserId())).thenReturn(List.of(projectUserEntity));
		when(projectUserPersistenceMapper.toProjectUserList(List.of(projectUserEntity))).thenReturn(List.of(projectUser));

		List<ProjectUser> result = projectUserPersistenceAdapter.findByUserId(projectUser.getUserId());

		assertThat(result).isEqualTo(List.of(projectUser));
		verify(projectUserRepository).findByUserId(projectUser.getUserId());
		verify(projectUserPersistenceMapper).toProjectUserList(List.of(projectUserEntity));
	}

	@Test
	void findByUid() {
		when(projectUserRepository.findByUid(projectUser.getUid())).thenReturn(projectUserEntity);
		when(projectUserPersistenceMapper.toDomain(projectUserEntity)).thenReturn(projectUser);

		ProjectUser result = projectUserPersistenceAdapter.findByUid(projectUser.getUid());

		assertThat(result).isEqualTo(projectUser);
		verify(projectUserRepository).findByUid(projectUser.getUid());
		verify(projectUserPersistenceMapper).toDomain(projectUserEntity);
	}

	@Test
	void existsByProjectUidAndUserId() {
		when(projectUserRepository.existsByProjectUidAndUserId(projectEntity.getUid(), projectUser.getUserId()))
			.thenReturn(true);

		boolean exists = projectUserPersistenceAdapter.existsByProjectUidAndUserId(projectEntity.getUid(), projectUser.getUserId());

		assertThat(exists).isTrue();
		verify(projectUserRepository).existsByProjectUidAndUserId(projectEntity.getUid(), projectUser.getUserId());
	}
}
