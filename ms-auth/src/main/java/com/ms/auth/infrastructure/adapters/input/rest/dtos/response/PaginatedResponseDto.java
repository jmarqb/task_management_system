package com.ms.auth.infrastructure.adapters.input.rest.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponseDto {

	@Schema(description = "total number of elements", example = "10")
	private int total;

	@Schema(description = "page number", example = "0")
	private int page;

	@Schema(description = "page size", example = "20")
	private int size;

	@Schema(description = "data", example = "[]")
	private List<?> data;

	@Schema(description = "timestamp", example = "2021-01-01T00:00:00.000Z")
	private LocalDateTime timestamp;
}
