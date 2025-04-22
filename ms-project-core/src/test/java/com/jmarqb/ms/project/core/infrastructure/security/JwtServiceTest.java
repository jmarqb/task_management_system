package com.jmarqb.ms.project.core.infrastructure.security;

import com.jmarqb.ms.project.core.infrastructure.security.service.JwtService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	private JwtService jwtService;

	private SecretKey returnedSecret;

	private String mockToken;
	private Claims claims;


	@BeforeEach
	void setUp() throws JsonProcessingException {
		String TEST_SECRET = "mi_testing_secret_value_key_123_456_789";
		returnedSecret = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
		jwtService = new JwtService(TEST_SECRET);

		List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));

		String username = "testUser";
		Long userId = 1L;

		claims = Jwts.claims()
			.add("authorities", new ObjectMapper().writeValueAsString(roles))
			.add("username", username)
			.add("id", userId)
			.add("iss", "ms-auth")
			.build();

		mockToken = Jwts.builder()
			.subject(username)
			.issuer("ms-auth")
			.claims(claims)
			.expiration(new Date(System.currentTimeMillis() + 1200000))
			.issuedAt(new Date())
			.signWith(returnedSecret)
			.compact();

	}

	@Test
	void extractAllClaims() {
		Claims returnedClaims = jwtService.extractAllClaims(mockToken);

		assertThat(returnedClaims).isNotNull();
		assertThat(jwtService.extractAllClaims(mockToken)).isEqualTo(returnedClaims);
	}

	@Test
	void isTokenValid() {
		Claims returnedClaims = jwtService.extractAllClaims(mockToken);
		assertThat(jwtService.isTokenValid(mockToken, returnedClaims.getSubject())).isTrue();
	}

	@Test
	void isTokenValid_ShouldReturnFalseForInvalidUsername() throws JsonProcessingException {
		assertThat(jwtService.isTokenValid(mockToken, "otherUser")).isFalse();
	}

	@Test
	void isTokenExpired() {
		String expiredToken = Jwts.builder()
			.subject("testUser")
			.expiration(new Date(System.currentTimeMillis() - 3800000)) // Expired token
			.issuedAt(new Date(System.currentTimeMillis() - 7200000))
			.signWith(returnedSecret)
			.compact();

		assertThat(jwtService.isTokenExpired(expiredToken)).isTrue();
	}
}