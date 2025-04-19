package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.application.ports.input.JwtUseCase;
import com.ms.auth.data.seed.TestDataInitializer;
import com.ms.auth.domain.model.CustomUserDetails;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.LoginDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.AuthResponseDto;
import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
class AuthControllerE2ETest {

	@Autowired
	private TestRestTemplate client;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUseCase jwtUseCase;

	private String token;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() throws JsonProcessingException {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ADMIN"));
		CustomUserDetails user = new CustomUserDetails(
			1L,
			"testadmin@example.com",
			"password",
			true,
			true,
			true,
			true,
			authorities
		);
		token = configureJwtToken(user);
		client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().set("Authorization", "Bearer " + token);
			return execution.execute(request, body);
		});
	}

	protected String configureJwtToken(CustomUserDetails user) throws JsonProcessingException {
		return jwtUseCase.generateToken(user);
	}

	@Test
	void login() {
		Optional<UserEntity> user = userRepository.findByEmail("testadmin@example.com");
		LoginDto loginDto = new LoginDto("testadmin@example.com", "password");

		ResponseEntity<AuthResponseDto> response = client.postForEntity(createURI("/api/auth/login"), loginDto, AuthResponseDto.class);

		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(jwtUseCase.isTokenValid(response.getBody().getToken(), user.get())).isTrue();
	}

	@Test
	void loginBadCredentials() {
		LoginDto loginDto = new LoginDto("testadmin@example.com", "badpassword");
		ResponseEntity<Error> response =
			client.postForEntity(createURI("/api/auth/login"), loginDto, Error.class);
		assertThat(response.getStatusCode().value()).isEqualTo(401);
	}

	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}
}
