package com.jmarqb.ms.project.core.infrastructure.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import io.jsonwebtoken.Claims;

public record CustomAuthenticationDetails(Claims claims, WebAuthenticationDetails webDetails) {
}

