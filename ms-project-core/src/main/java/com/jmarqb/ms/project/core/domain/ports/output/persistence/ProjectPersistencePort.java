package com.jmarqb.ms.project.core.domain.ports.output.persistence;

import org.springframework.data.domain.Pageable;

import java.util.List;

import com.jmarqb.ms.project.core.domain.model.Project;

public interface ProjectPersistencePort {

	Project save(Project project);

	List<Project> searchAll(Pageable pageable, Long userId);

	Project findByUid(String uid);


}
