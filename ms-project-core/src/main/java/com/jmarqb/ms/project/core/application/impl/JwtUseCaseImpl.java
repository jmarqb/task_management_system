package com.jmarqb.ms.project.core.application.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.jmarqb.ms.project.core.application.ports.input.JwtUseCase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtUseCaseImpl implements JwtUseCase {
	private final SecretKey SECRET_KEY;

	public JwtUseCaseImpl(@Value("${jwt.private}") String secret) {
		SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith(SECRET_KEY)
			.requireIssuer("ms-auth")
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	@Override
	public boolean isTokenValid(String token, String subject) {
		Claims claims = extractAllClaims(token);
		String username = claims.getSubject();
		return username.equals(subject) && !isTokenExpired(token);
	}

	@Override
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
