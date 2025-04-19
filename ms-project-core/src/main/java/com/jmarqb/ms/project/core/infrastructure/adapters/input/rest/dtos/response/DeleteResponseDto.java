package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Delete response.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delete response")
public class DeleteResponseDto {
	@Schema(description = "Acknowledged", example = "true")
	private boolean acknowledged;

	@Schema(description = "Deleted count", example = "1")
	private int deletedCount;
}
