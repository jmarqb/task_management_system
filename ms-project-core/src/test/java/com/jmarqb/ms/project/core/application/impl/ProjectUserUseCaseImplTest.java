package com.jmarqb.ms.project.core.application.impl;

import java.util.UUID;

import com.jmarqb.ms.project.core.application.exceptions.ProjectUserNotFoundException;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectUserUseCaseImplTest {

	private @Mock ProjectUserPersistencePort projectUserPersistencePort;

	private @InjectMocks ProjectUserUseCaseImpl projectUserUseCase;

	@Test
	void save() {
		ProjectUser inputProjectUser = Instancio.create(ProjectUser.class);
		inputProjectUser.setUid(null);

		when(projectUserPersistencePort.save(any(ProjectUser.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		ProjectUser savedProjectUser = projectUserUseCase.save(inputProjectUser);

		assertThat(savedProjectUser.getUid()).isNotNull();
		assertThat(savedProjectUser.getUid()).hasSize(36);
		assertThat(savedProjectUser).usingRecursiveComparison()
			.ignoringFields("uid")
			.isEqualTo(inputProjectUser);
		verify(projectUserPersistencePort).save(savedProjectUser);
	}

	@Test
	void findByUid() {
		ProjectUser expectedProjectUser = Instancio.create(ProjectUser.class);

		when(projectUserPersistencePort.findByUid(expectedProjectUser.getUid()))
			.thenReturn(expectedProjectUser);

		ProjectUser result = projectUserUseCase.findByUid(expectedProjectUser.getUid());

		assertThat(result).isEqualTo(expectedProjectUser);
		verify(projectUserPersistencePort).findByUid(expectedProjectUser.getUid());
	}

	@Test
	void findByUid_NotFound() {
		String uid = UUID.randomUUID().toString();

		when(projectUserPersistencePort.findByUid(uid)).thenReturn(null);

		assertThatThrownBy(() -> projectUserUseCase.findByUid(uid))
			.isInstanceOf(ProjectUserNotFoundException.class)
			.hasMessage("ProjectUser with id %s not found".formatted(uid));

		verify(projectUserPersistencePort).findByUid(uid);
	}

	@Test
	void findByProjectUidAndUserId() {
		ProjectUser expected = Instancio.create(ProjectUser.class);
		String projectUid = expected.getProject().getUid();
		Long userId = expected.getUserId();

		when(projectUserPersistencePort.findByProjectUidAndUserId(projectUid, userId))
			.thenReturn(expected);

		ProjectUser result = projectUserUseCase.findByProjectUidAndUserId(projectUid, userId);

		assertThat(result).isEqualTo(expected);
		verify(projectUserPersistencePort).findByProjectUidAndUserId(projectUid, userId);
	}

	@Test
	void findByProjectUidAndUserId_NotFound() {
		String projectUid = UUID.randomUUID().toString();
		Long userId = 1L;

		when(projectUserPersistencePort.findByProjectUidAndUserId(projectUid, userId))
			.thenReturn(null);

		assertThatThrownBy(() -> projectUserUseCase.findByProjectUidAndUserId(projectUid, userId))
			.isInstanceOf(ProjectUserNotFoundException.class)
			.hasMessage("Project with id %s not found for user with id %s".formatted(projectUid, userId));

		verify(projectUserPersistencePort).findByProjectUidAndUserId(projectUid, userId);
	}

	@Test
	void delete() {
		ProjectUser projectUser = Instancio.create(ProjectUser.class);
		projectUser.setDeleted(false);
		projectUser.setDeletedAt(null);

		when(projectUserPersistencePort.findByUid(projectUser.getUid()))
			.thenReturn(projectUser);

		when(projectUserPersistencePort.save(any(ProjectUser.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		projectUserUseCase.delete(projectUser.getUid());

		assertThat(projectUser.isDeleted()).isTrue();
		assertThat(projectUser.getDeletedAt()).isNotNull();
		verify(projectUserPersistencePort).save(projectUser);
	}

	@Test
	void existsByProjectUidAndUserId() {
		String projectUid = UUID.randomUUID().toString();
		Long userId = 123L;

		when(projectUserPersistencePort.existsByProjectUidAndUserId(projectUid, userId))
			.thenReturn(true);

		boolean result = projectUserUseCase.existsByProjectUidAndUserId(projectUid, userId);

		assertThat(result).isTrue();
		verify(projectUserPersistencePort).existsByProjectUidAndUserId(projectUid, userId);
	}
}