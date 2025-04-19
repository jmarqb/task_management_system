package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.project.core.data.seed.TestDataInitializer;
import com.jmarqb.ms.project.core.domain.model.Error;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateProjectDto;
import com.jmarqb.ms.project.core.infrastructure.security.config.SecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SecurityConfig.class, TestDataInitializer.class})
class HandlerExceptionControllerTest {

	@Autowired
	private TestRestTemplate client;

	@LocalServerPort
	private int port;

	@Test
	void handleInvalidTokenException() throws JsonProcessingException {
		String TEST_SECRET = "M1_Testing_secret_ECOMMERCE_SECRET_KEY";
		SecretKey secret = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));

		List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("USER"),
			new SimpleGrantedAuthority("ADMIN"));

		Long userId = 1L;

		Claims claims = Jwts.claims()
			.add("authorities", new ObjectMapper().writeValueAsString(roles))
			.add("username", null)
			.add("id", userId)
			.add("iss", "ms-auth")
			.build();

		String mockToken = Jwts.builder()
			.subject(null)
			.issuer("ms-auth")
			.claims(claims)
			.expiration(new Date(System.currentTimeMillis() + 1200000))
			.issuedAt(new Date())
			.signWith(secret)
			.compact();


		client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().set("Authorization", "Bearer " + mockToken);
			return execution.execute(request, body);
		});

		CreateProjectDto createProjectDto = CreateProjectDto.builder()
			.name("Test Project")
			.description("Teste description")
			.build();

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/v1/projects"),
			createProjectDto, Error.class);


		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		Error error = response.getBody();

		assertThat(error).isNotNull();

		assertThat(error.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(error.getError()).isEqualTo("Unauthorized");
		assertThat(error.getMessage()).isEqualTo("Unauthorized");
	}


	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}
}