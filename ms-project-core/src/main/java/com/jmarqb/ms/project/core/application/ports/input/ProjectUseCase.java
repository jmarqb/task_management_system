package com.jmarqb.ms.project.core.application.ports.input;

import com.jmarqb.ms.project.core.domain.model.Project;

import java.util.List;

public interface ProjectUseCase {

	Project save(Project project);

	Project findProjectByUid(String uid);

	List<Project> searchAll(int page, int size, String sort, Long userId);

	Project updateProject(Project project, Long userId);

	Project addMembersToProject(String projectUid, List<Long> userIds, Long requesterUserId);

	Project removeMemberFromProject(String projectUid, Long memberIdToRemove, Long requesterUserId);

	void deleteProject(String uid, Long userId);

	boolean isArchived(String uid);
}
