package com.ms.auth.infrastructure.adapters.input.rest.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponseDto {
	@Schema(description = "Acknowledged", example = "true")
	private boolean acknowledged;

	@Schema(description = "Deleted count", example = "1")
	private int deletedCount;
}
