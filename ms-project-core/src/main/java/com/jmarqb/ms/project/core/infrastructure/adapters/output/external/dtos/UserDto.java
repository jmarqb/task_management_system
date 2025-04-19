package com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
	Long id;

	String firstName;

	String lastName;

	String email;
}
