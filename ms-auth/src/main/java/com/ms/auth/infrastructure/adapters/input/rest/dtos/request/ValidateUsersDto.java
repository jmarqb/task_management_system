package com.ms.auth.infrastructure.adapters.input.rest.dtos.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidateUsersDto {

	@ArraySchema(arraySchema = @Schema(implementation = Long.class, description = "Users ids"))
	List<Long> usersIds;
}
