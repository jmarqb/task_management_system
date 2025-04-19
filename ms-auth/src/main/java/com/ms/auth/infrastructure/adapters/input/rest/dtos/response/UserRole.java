package com.ms.auth.infrastructure.adapters.input.rest.dtos.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRole {

	private String name;

	private String description;

	private String icon;

	private Boolean isAdmin;

	private Boolean isDefaultRole;
}
