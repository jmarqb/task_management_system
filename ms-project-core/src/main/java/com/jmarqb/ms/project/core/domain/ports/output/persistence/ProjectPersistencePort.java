package com.jmarqb.ms.project.core.domain.ports.output.persistence;

import com.jmarqb.ms.project.core.domain.model.Pagination;
import com.jmarqb.ms.project.core.domain.model.Project;

import java.util.List;

public interface ProjectPersistencePort {

	Project save(Project project);

	List<Project> searchAll(Pagination pagination, Long userId);

	Project findByUid(String uid);


}
