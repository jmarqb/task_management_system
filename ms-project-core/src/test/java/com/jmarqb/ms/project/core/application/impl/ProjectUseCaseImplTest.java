package com.jmarqb.ms.project.core.application.impl;

import com.jmarqb.ms.project.core.application.exceptions.InvalidProjectForUserException;
import com.jmarqb.ms.project.core.application.exceptions.ProjectNotFoundException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedProjectException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedTaskAccessException;
import com.jmarqb.ms.project.core.application.mapper.UpdateFieldMapper;
import com.jmarqb.ms.project.core.application.vo.ProjectUserRole;
import com.jmarqb.ms.project.core.domain.model.Pagination;
import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.model.ProjectUser;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectPersistencePort;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.ProjectUserPersistencePort;
import com.jmarqb.ms.project.core.domain.ports.output.persistence.TaskPersistencePort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectUseCaseImplTest {

	@Mock
	private ProjectPersistencePort projectPersistencePort;
	@Mock
	private ProjectUserPersistencePort projectUserPersistencePort;
	@Mock
	private TaskPersistencePort taskPersistencePort;

	private @Mock UpdateFieldMapper updateFieldMapper;

	@InjectMocks
	private ProjectUseCaseImpl projectUseCase;


	@Test
	void save() {
		Project project = Instancio.create(Project.class);
		project.setUid(null);

		when(projectPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(projectUserPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Project result = projectUseCase.save(project);

		assertThat(result.getUid()).isNotNull();
		verify(projectPersistencePort).save(result);
		verify(projectUserPersistencePort).save(any(ProjectUser.class));
	}

	@Test
	void findProjectByUid() {
		Project project = Instancio.create(Project.class);
		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);

		Project found = projectUseCase.findProjectByUid(project.getUid());
		assertThat(found).isEqualTo(project);
	}

	@Test
	void findProjectByUidNotFound() {
		String uid = UUID.randomUUID().toString();
		when(projectPersistencePort.findByUid(uid)).thenThrow(new ProjectNotFoundException("Not found"));

		assertThatThrownBy(() -> projectUseCase.findProjectByUid(uid))
			.isInstanceOf(ProjectNotFoundException.class);
	}

	@Test
	void searchAll() {
		List<Project> expectedProjects = List.of(Instancio.create(Project.class));
		when(projectPersistencePort.searchAll(any(Pagination.class), eq(1L))).thenReturn(expectedProjects);

		List<Project> result = projectUseCase.searchAll(0, 10, "asc", 1L);
		assertThat(result).isEqualTo(expectedProjects);
	}

	@Test
	void updateProject() {
		Project existing = Instancio.create(Project.class);
		Project update = Instancio.create(Project.class);
		update.setUid(existing.getUid());
		update.setOwnerId(existing.getOwnerId());

		when(projectPersistencePort.findByUid(update.getUid())).thenReturn(existing);

		doNothing().when(updateFieldMapper).updateProject(update, existing);

		when(projectPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Project result = projectUseCase.updateProject(update, existing.getOwnerId());
		assertThat(result).isNotNull();
		assertThat(result.getUid()).isEqualTo(existing.getUid());
		assertThat(result.getOwnerId()).isEqualTo(existing.getOwnerId());
		verify(projectPersistencePort).save(existing);
	}

	@Test
	void updateProjectUnauthorized() {
		Project project = Instancio.create(Project.class);
		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);

		assertThatThrownBy(() -> projectUseCase.updateProject(project, project.getOwnerId() + 1))
			.isInstanceOf(UnauthorizedProjectException.class);
	}

	@Test
	void addMembersToProject() {
		Project project = Instancio.create(Project.class);
		ProjectUser owner = ProjectUser.builder()
			.project(project)
			.userId(project.getOwnerId())
			.role(ProjectUserRole.OWNER.name())
			.build();

		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);
		when(projectUserPersistencePort.findByProjectUidAndUserId(project.getUid(), owner.getUserId())).thenReturn(owner);
		when(projectUserPersistencePort.existsByProjectUidAndUserId(anyString(), anyLong())).thenReturn(false);
		when(projectUserPersistencePort.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

		Project result = projectUseCase.addMembersToProject(project.getUid(), List.of(100L, 101L), owner.getUserId());
		assertThat(result.getMembers()).isNotNull();
	}

	@Test
	void addMembersToProjectUnauthorized() {
		Project project = Instancio.create(Project.class);
		ProjectUser notOwner = ProjectUser.builder()
			.project(project)
			.userId(999L)
			.role(ProjectUserRole.MEMBER.name())
			.build();

		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);
		when(projectUserPersistencePort.findByProjectUidAndUserId(project.getUid(), notOwner.getUserId())).thenReturn(notOwner);

		assertThatThrownBy(() -> projectUseCase.addMembersToProject(project.getUid(), List.of(1L), notOwner.getUserId()))
			.isInstanceOf(UnauthorizedTaskAccessException.class);
	}

	@Test
	void addMembersToProjectIllegalArgumentException() {
		Project project = Instancio.create(Project.class);
		ProjectUser owner = ProjectUser.builder()
			.project(project)
			.userId(project.getOwnerId())
			.role(ProjectUserRole.OWNER.name())
			.build();
		List<Long> ids = new ArrayList<>();

		assertThatThrownBy(() -> projectUseCase.addMembersToProject(project.getUid(), ids, owner.getUserId()))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void removeMemberFromProject() {
		Project project = Instancio.create(Project.class);
		Long requester = project.getOwnerId();
		Long memberId = 200L;

		ProjectUser owner = ProjectUser.builder()
			.userId(requester)
			.role(ProjectUserRole.OWNER.name())
			.project(project)
			.build();

		ProjectUser member = ProjectUser.builder()
			.userId(memberId)
			.uid(UUID.randomUUID().toString())
			.role(ProjectUserRole.MEMBER.name())
			.project(project)
			.build();

		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);
		when(projectUserPersistencePort.findByProjectUidAndUserId(project.getUid(), requester)).thenReturn(owner);
		when(projectUserPersistencePort.findByProjectUidAndUserId(project.getUid(), memberId)).thenReturn(member);
		when(taskPersistencePort.findByProjectUidAndAssignedUserId(project.getUid(), memberId)).thenReturn(List.of());
		when(projectUserPersistencePort.save(any())).thenReturn(member);

		Project result = projectUseCase.removeMemberFromProject(project.getUid(), memberId, requester);
		assertThat(result).isNotNull();
	}

	@Test
	void removeMemberFromProjectUnauthorized() {
		Project project = Instancio.create(Project.class);
		ProjectUser requester = ProjectUser.builder()
			.userId(999L)
			.role(ProjectUserRole.MEMBER.name())
			.project(project)
			.build();

		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);
		when(projectUserPersistencePort.findByProjectUidAndUserId(project.getUid(), requester.getUserId())).thenReturn(requester);

		assertThatThrownBy(() -> projectUseCase.removeMemberFromProject(project.getUid(), 100L, requester.getUserId()))
			.isInstanceOf(UnauthorizedTaskAccessException.class);
	}

	@Test
	void removeMemberFromProjectInvalidProjectForUserException() {
		Project project = Instancio.create(Project.class);
		ProjectUser requester = ProjectUser.builder()
			.userId(999L)
			.role(ProjectUserRole.OWNER.name())
			.project(project)
			.build();

		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);

		assertThatThrownBy(() -> projectUseCase.removeMemberFromProject(project.getUid(), 100L, requester.getUserId()))
			.isInstanceOf(InvalidProjectForUserException.class);
	}

	@Test
	void removeMemberThrowException_CannotRemoveThemselves() {
		Project project = Instancio.create(Project.class);
		ProjectUser requester = ProjectUser.builder()
			.userId(project.getOwnerId())
			.role(ProjectUserRole.OWNER.name())
			.project(project)
			.build();

		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);
		when(projectUserPersistencePort.findByProjectUidAndUserId(project.getUid(), requester.getUserId())).thenReturn(requester);

		assertThatThrownBy(() -> projectUseCase.removeMemberFromProject(project.getUid(), requester.getUserId(), project.getOwnerId()))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void deleteProject() {
		Project project = Instancio.create(Project.class);
		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);

		projectUseCase.deleteProject(project.getUid(), project.getOwnerId());
		verify(projectPersistencePort).save(project);
		assertThat(project.isDeleted()).isTrue();
	}

	@Test
	void deleteProjectUnauthorized() {
		Project project = Instancio.create(Project.class);
		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);

		assertThatThrownBy(() -> projectUseCase.deleteProject(project.getUid(), 999L))
			.isInstanceOf(UnauthorizedProjectException.class);
	}

	@Test
	void isArchived() {
		Project project = Instancio.create(Project.class);
		when(projectPersistencePort.findByUid(project.getUid())).thenReturn(project);

		boolean archived = projectUseCase.isArchived(project.getUid());
		assertThat(archived).isEqualTo(project.isArchived());
	}

}