package com.ms.auth.domain.model;

public record Pagination(int page, int size, String sort, String sortBy) {
}
