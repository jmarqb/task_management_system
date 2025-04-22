package com.jmarqb.ms.project.core.infrastructure.adapters.output.external;

import com.jmarqb.ms.project.core.domain.ports.output.external.User;
import com.jmarqb.ms.project.core.domain.ports.output.external.UserClient;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.UserDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.ValidateUsersDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.mapper.UserMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

	private final UserServiceFeignClient userServiceFeignClient;
	private final UserMapper userMapper;

	@Override
	public List<User> checkUsers(List<Long> usersIds) {
		ValidateUsersDto validateUsersDto = new ValidateUsersDto();
		validateUsersDto.setUsersIds(usersIds);
		Set<UserDto> dto = userServiceFeignClient.checkUsersIds(validateUsersDto);
		return dto.stream().map(userMapper::toDomain).toList();

	}
}
