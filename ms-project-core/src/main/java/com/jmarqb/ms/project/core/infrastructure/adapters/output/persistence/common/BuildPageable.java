package com.jmarqb.ms.project.core.infrastructure.adapters.output.persistence.common;

import com.jmarqb.ms.project.core.domain.model.Pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class BuildPageable {
	public static Pageable buildPageable(Pagination pagination) {
		return PageRequest.of(pagination.page(), pagination.size(), "asc".equalsIgnoreCase(pagination.sort()) ?
			Sort.Direction.ASC : Sort.Direction.DESC, pagination.sortBy());
	}
}
