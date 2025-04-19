package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectTask {
	private String uid;

	private String name;

	private String status;

	private String priority;

	private String assignedUserId;

}
