package com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateUsersDto {

	@NotEmpty(message = "The list of user IDs must not be empty")
	@ArraySchema(arraySchema = @Schema(implementation = Long.class, description = "Users ids"))
	List<Long> usersIds;
}
