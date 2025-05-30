package com.jmarqb.ms.project.core.infrastructure.adapters.output.external.mapper;

import com.jmarqb.ms.project.core.domain.ports.output.external.User;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.UserDto;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public User toDomain(UserDto userDto) {
		return new User(userDto.getId(), userDto.getFirstName(), userDto.getLastName(), userDto.getEmail());
	}
}
