package com.ms.auth.infrastructure.adapters.input.rest.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartialUserResponseDto {

	@Schema(description = "id", example = "1")
	private Long id;

	@Schema(description = "firstName", example = "John")
	private String firstName;

	@Schema(description = "lastName", example = "Doe")
	private String lastName;

	@Schema(description = "email", example = "oR7o0@example.com")
	private String email;
}
