package com.ms.auth.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.infrastructure.security.model.CustomUserDetails;
import com.ms.auth.infrastructure.security.service.JwtService;
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

	private CustomUserDetails mockUser;

	@BeforeEach
	void setUp() {
		String TEST_SECRET = "mi_testing_secret_value_key_123_456_789";
		returnedSecret = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
		jwtService = new JwtService(TEST_SECRET);

		List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		mockUser = new CustomUserDetails(
			1L,
			"testUser",
			"password",
			true,
			true,
			true,
			true,
			roles);
	}

	@Test
	void generateToken_ShouldReturnValidToken() throws JsonProcessingException {

		String token = jwtService.generateToken(mockUser);
		assertThat(token).isNotNull();
		assertThat(token.isEmpty()).isFalse();

		Claims claims = Jwts.parser()
			.verifyWith(returnedSecret)
			.build()
			.parseSignedClaims(token)
			.getPayload();

		assertThat(claims.get("username")).isEqualTo("testUser");
		assertThat(claims.get("authorities")).isNotNull();
	}

	@Test
	void extractUsername_ShouldReturnCorrectUsername() throws JsonProcessingException {
		CustomUserDetails user = mockUser;

		String token = jwtService.generateToken(user);
		String extractedUsername = jwtService.extractUsername(token);

		assertThat(extractedUsername).isEqualTo("testUser");
	}

	@Test
	void isTokenValid_ShouldReturnTrueForValidToken() throws JsonProcessingException {
		CustomUserDetails userDetails = mockUser;

		String token = jwtService.generateToken(userDetails);

		assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
	}

	@Test
	void isTokenValid_ShouldReturnFalseForInvalidUsername() throws JsonProcessingException {
		CustomUserDetails otherUser = new CustomUserDetails(
			2L,
			"otherUser",
			"password",
			true,
			true,
			true,
			true,
			Collections.emptyList()
		);

		String token = jwtService.generateToken(mockUser);

		assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
	}

	@Test
	void isTokenExpired_ShouldReturnFalseForNewToken() throws JsonProcessingException {

		String token = jwtService.generateToken(mockUser);

		assertThat(jwtService.isTokenExpired(token)).isFalse();
	}

	@Test
	void isTokenExpired_ShouldReturnTrueForExpiredToken() {
		String expiredToken = Jwts.builder()
			.subject("testUser")
			.expiration(new Date(System.currentTimeMillis() - 3800000)) // Expired token
			.issuedAt(new Date(System.currentTimeMillis() - 7200000))
			.signWith(returnedSecret)
			.compact();

		assertThat(jwtService.isTokenExpired(expiredToken)).isTrue();
	}
}
