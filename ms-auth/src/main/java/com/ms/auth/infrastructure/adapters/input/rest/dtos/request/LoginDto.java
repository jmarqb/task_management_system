package com.ms.auth.infrastructure.adapters.input.rest.dtos.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

	@Schema(description = "Email must be a valid email", example = "oR7o0@example.com")
	@NotBlank(message = "username is required")
	private String email;

	@Schema(description = "password must be between 4 and 50 characters", example = "1234")
	@NotBlank(message = "password is required")
	private String password;
}
