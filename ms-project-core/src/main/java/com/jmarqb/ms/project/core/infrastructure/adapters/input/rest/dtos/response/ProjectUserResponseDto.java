package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProjectUserResponseDto {
	private Long userId;
	private String role;
}
