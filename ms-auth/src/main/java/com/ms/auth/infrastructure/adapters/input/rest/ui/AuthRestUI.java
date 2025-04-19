package com.ms.auth.infrastructure.adapters.input.rest.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.LoginDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.AuthResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.OpenApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User login", description = "Endpoint for user login")
public interface AuthRestUI {

	@PostMapping("/login")
	@ApiResponse(responseCode = "200", description = "User logged in successfully")
	@ApiResponse(responseCode = "401", description = "Invalid credentials",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
			examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE)))
	ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto loginRequest);
}
