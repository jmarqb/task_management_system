package com.ms.auth.infrastructure.adapters.input.rest.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ms.auth.infrastructure.adapters.validation.ExistEmail;
import com.ms.auth.infrastructure.adapters.validation.ExistPhone;
import com.ms.auth.infrastructure.adapters.validation.ValueOfEnum;
import com.ms.auth.infrastructure.adapters.vo.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Data
public class UpdateUserDto {

	@JsonIgnore
	@Schema(hidden = true)
	private Long id;

	@Schema(description = "firstName must be between 3 and 30 characters", example = "John")
	@NotBlank
	@Size(min = 3, max = 30, message = "firstName must be between 3 and 30 characters")
	private String firstName;

	@Schema(description = "lastName must be between 3 and 30 characters", example = "Doe")
	@NotBlank
	@Size(min = 3, max = 30, message = "lastName must be between 3 and 30 characters")
	private String lastName;

	@Schema(description = "Email must be a valid email", example = "oR7o0@example.com")
	@NotBlank
	@Email
	@ExistEmail
	private String email;

	@Schema(description = "Age must be at least 18", example = "20")
	@Min(value = 18, message = "Age must be at least 18")
	private int age;

	@Schema(description = "Phone must be a valid phone number", example = "+1234567890")
	@NotBlank
	@ExistPhone
	@Pattern(regexp = "^((\\+[1-9]{1,4}[ -]?)|(\\([0-9]{2,3}\\)[ -]?)|([0-9]{2,4})[ -]?)*?[0-9]{3,4}[ -]?[0-9]{3,4}$",
		message = "Phone must be a valid phone number")
	private String phone;

	@Schema(description = "Gender must be [MALE|FEMALE|NO_DIFFERENTIATION|NO_IDENTIFY_ANY]", example = "MALE")
	@NotBlank
	@ValueOfEnum(enumClass = Gender.class, message = "Gender must be [MALE|FEMALE|NO_DIFFERENTIATION|NO_IDENTIFY_ANY]")
	private String gender;

	@Schema(description = "country", example = "Colombia")
	@NotBlank
	private String country;

}
