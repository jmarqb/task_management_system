package com.jmarqb.ms.project.core.infrastructure.security.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtService {
	private final SecretKey SECRET_KEY;

	public JwtService(@Value("${jwt.private}") String secret) {
		SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith(SECRET_KEY)
			.requireIssuer("ms-auth")
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public boolean isTokenValid(String token, String subject) {
		Claims claims = extractAllClaims(token);
		String username = claims.getSubject();
		return username.equals(subject) && !isTokenExpired(token);
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
