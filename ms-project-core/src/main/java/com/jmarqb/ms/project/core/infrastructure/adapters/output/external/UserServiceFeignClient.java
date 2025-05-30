package com.jmarqb.ms.project.core.infrastructure.adapters.output.external;

import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.config.FeignClientConfiguration;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.UserDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.ValidateUsersDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.Set;

@FeignClient(name = "ms-auth-service", url = "${API_GATEWAY_URI:http://localhost:8080}",
	configuration = FeignClientConfiguration.class)
public interface UserServiceFeignClient {

	@PostMapping("/api/users/validate")
	Set<UserDto> checkUsersIds(@Valid @RequestBody ValidateUsersDto validateUsersDto);
}
