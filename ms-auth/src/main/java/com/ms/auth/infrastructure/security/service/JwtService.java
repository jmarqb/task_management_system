package com.ms.auth.infrastructure.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.auth.infrastructure.security.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class JwtService {
	private final SecretKey SECRET_KEY;

	public JwtService(@Value("${jwt.private}") String secret) {
		SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(CustomUserDetails user) throws JsonProcessingException {
		String username = user.getUsername();
		Collection<GrantedAuthority> roles = user.getAuthorities();

		Claims claims = Jwts.claims()
			.add("authorities", new ObjectMapper().writeValueAsString(roles))
			.add("username", username)
			.add("id", user.getId())
			.build();

		return Jwts.builder()
			.subject(username)
			.issuer("ms-auth")
			.claims(claims)
			.expiration(new Date(System.currentTimeMillis() + 3600000))
			.issuedAt(new Date())
			.signWith(SECRET_KEY)
			.compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser()
			.verifyWith(SECRET_KEY)
			.requireIssuer("ms-auth")
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public boolean isTokenExpired(String token) {
		try {
			Date expiration = Jwts.parser()
				.verifyWith(SECRET_KEY)
				.requireIssuer("ms-auth")
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration();
			return expiration.before(new Date());
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			return true;
		}
	}
}
