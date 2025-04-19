package com.jmarqb.ms.project.core.data.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.jmarqb.ms.project.core.application.enums.PriorityStatus;
import com.jmarqb.ms.project.core.application.enums.ProjectUserRole;
import com.jmarqb.ms.project.core.application.enums.TaskStatus;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.ProjectUserEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.model.TaskEntity;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.ProjectRepository;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.ProjectUserRepository;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.repository.TaskRepository;
import static com.jmarqb.ms.project.core.data.util.Util.projectUId;
import static com.jmarqb.ms.project.core.data.util.Util.projectUserUId;
import static com.jmarqb.ms.project.core.data.util.Util.taskUid;

@Component
@Profile("test")
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {


	private final ProjectRepository projectRepository;

	private final ProjectUserRepository projectUserRepository;

	private final TaskRepository taskRepository;


	@Getter
	private ProjectEntity project;

	@Getter
	private TaskEntity task;

	@Getter
	private ProjectUserEntity projectUser;


	@Override
	public void run(String... args) {
		project = ProjectEntity.builder()
			.name("Test Project")
			.description("Test Project Description")
			.archived(false)
			.ownerId(1L)
			.uid(projectUId)
			.deleted(false)
			.deletedAt(null)
			.members(new ArrayList<>())
			.tasks(new ArrayList<>())
			.build();

		projectRepository.save(project);

		task = TaskEntity.builder()
			.name("Test Task")
			.status(TaskStatus.PENDING)
			.priority(PriorityStatus.MEDIUM)
			.project(project)
			.assignedUserId(1L)
			.uid(taskUid)
			.deleted(false)
			.deletedAt(null)
			.build();

		taskRepository.save(task);

		projectUser = ProjectUserEntity.builder()
			.uid(projectUserUId)
			.userId(1L)
			.project(project)
			.role(ProjectUserRole.OWNER)
			.deleted(false)
			.deletedAt(null)
			.build();

		projectUserRepository.save(projectUser);

		project.getMembers().add(projectUser);
		project.getTasks().add(task);
		projectRepository.save(project);


	}
}


