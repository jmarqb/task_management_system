package com.ms.auth.infrastructure.adapters.input.rest.dtos.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ms.auth.application.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponseDto {

	@Schema(description = "id", example = "1")
	private Long id;

	@Schema(description = "firstName", example = "John")
	private String firstName;

	@Schema(description = "lastName", example = "Doe")
	private String lastName;

	@Schema(description = "email", example = "oR7o0@example.com")
	private String email;

	@Schema(description = "age", example = "20")
	private int age;

	@Schema(description = "phone", example = "+1234567890")
	private String phone;

	@Schema(description = "gender", example = "MALE")
	private Gender gender;

	@Schema(description = "country", example = "Colombia")
	private String country;

	@Schema(description = "deleted", example = "false")
	private boolean deleted;

	@Schema(description = "deletedAt", example = "null")
	private Date deletedAt;

	private List<UserRole> roles;

}
