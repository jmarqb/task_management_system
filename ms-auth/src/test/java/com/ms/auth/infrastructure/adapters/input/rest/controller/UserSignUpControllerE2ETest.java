package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import com.ms.auth.application.ports.input.UserUseCase;
import com.ms.auth.data.seed.TestDataInitializer;
import com.ms.auth.infrastructure.adapters.input.rest.advice.Error;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import org.junit.jupiter.api.Test;
import static com.ms.auth.data.Data.createUserDto;
import static com.ms.auth.data.Data.createUserResponseDto;
import static com.ms.auth.data.Data.getUserRole;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
class UserSignUpControllerE2ETest {

	@Autowired
	private TestRestTemplate client;

	@Autowired
	private UserUseCase userService;

	@LocalServerPort
	private int port;


	@Test
	void signUp() {
		CreateUserDto createUserDto = createUserDto();
		createUserDto.setEmail("newtestuser@example.com");
		createUserDto.setPhone("+789456123");
		CreateUserResponseDto createUserResponseDto = createUserResponseDto(2L);
		createUserResponseDto.setRoles(List.of(getUserRole(2L)));
		createUserResponseDto.setPhone(createUserDto.getPhone());
		createUserResponseDto.setEmail(createUserDto.getEmail());

		ResponseEntity<CreateUserResponseDto> response = client.postForEntity(createURI("/api/auth/signup"),
			createUserDto, CreateUserResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		CreateUserResponseDto body = response.getBody();

		assertThat(body).isNotNull();

		assertThat(body.getFirstName()).isEqualTo(createUserResponseDto.getFirstName());
		assertThat(body.getLastName()).isEqualTo(createUserResponseDto.getLastName());
		assertThat(body.getEmail()).isEqualTo(createUserResponseDto.getEmail());
		assertThat(body.getId()).isEqualTo(createUserResponseDto.getId());
		assertThat(body.getDeletedAt()).isEqualTo(createUserResponseDto.getDeletedAt());
		assertThat(body.isDeleted()).isEqualTo(createUserResponseDto.isDeleted());
		assertThat(body.getGender()).isEqualTo(createUserResponseDto.getGender());
		assertThat(body.getCountry()).isEqualTo(createUserResponseDto.getCountry());
		assertThat(body.getPhone()).isEqualTo(createUserResponseDto.getPhone());

	}

	@Test
	void signUpThrowBadRequest() {
		CreateUserDto createUserDto = createUserDto();
		createUserDto.setFirstName(null);

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/auth/signup"),
			createUserDto, Error.class);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		Error error = response.getBody();

		assertThat(error).isNotNull();

		assertThat(error.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(error.getError()).isEqualTo("Bad Request");
		assertThat(error.getMessage()).isEqualTo("Validation failed");
		assertThat(error.getFieldErrors().getFirst().getField()).isEqualTo("firstName");
		assertThat(error.getFieldErrors().getFirst().getRejectedValue()).isEqualTo("null");
	}

	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}

}
