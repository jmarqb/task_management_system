package com.ms.auth.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Role {

	private Long id;

	private String name;

	private String description;

	private String icon;

	private boolean isAdmin;

	private boolean isDefaultRole;

	private boolean deleted;

	private LocalDateTime deletedAt;

	private List<User> users;
}
