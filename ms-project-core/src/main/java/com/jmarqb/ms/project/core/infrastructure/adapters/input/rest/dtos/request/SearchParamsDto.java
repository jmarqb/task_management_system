package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request;

import com.jmarqb.ms.project.core.application.vo.Sort;
import com.jmarqb.ms.project.core.infrastructure.adapters.validation.ValueOfEnum;

import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchParamsDto {


	@Schema(hidden = true)
	private Long userId;

	@Schema(description = "page number", example = "0")
	private int page = 0;

	@Schema(description = "page size", example = "20")
	@Min(value = 1, message = "Size must be at least 1")
	private int size = 20;

	@Schema(description = "sort", example = "ASC")
	@ValueOfEnum(enumClass = Sort.class, message = "Sort must be [ASC|DESC]")
	private String sort;

}
