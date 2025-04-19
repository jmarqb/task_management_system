package com.jmarqb.ms.project.core.application.ports.input;

import io.jsonwebtoken.Claims;

public interface JwtUseCase {

	Claims extractAllClaims(String token);

	boolean isTokenValid(String token, String subject);

	boolean isTokenExpired(String token);
}
