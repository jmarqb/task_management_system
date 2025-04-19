package com.jmarqb.ms.project.core.infrastructure.security.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.project.core.application.ports.input.JwtUseCase;
import com.jmarqb.ms.project.core.infrastructure.security.CustomAuthenticationDetails;
import io.jsonwebtoken.Claims;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUseCase jwtUseCase;

	public JwtAuthenticationFilter(JwtUseCase jwtService
	) {
		this.jwtUseCase = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String HEADER_AUTHORIZATION = "Authorization";

		String PREFIX_TOKEN = "Bearer ";

		String header = request.getHeader(HEADER_AUTHORIZATION);

		if (header != null && header.startsWith(PREFIX_TOKEN)) {

			String token = header.replace(PREFIX_TOKEN, "");

			Claims claims = jwtUseCase.extractAllClaims(token);
			String username = claims.getSubject();
			String rawAuthorities = claims.get("authorities", String.class);

			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, String>> roles = mapper.readValue(rawAuthorities,
				new TypeReference<List<Map<String, String>>>() {
				});

			Collection<GrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority(role.get("authority")))
				.collect(Collectors.toList());

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				if (jwtUseCase.isTokenValid(token, username)) {
					UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(
							username, null, authorities);

					CustomAuthenticationDetails details = new CustomAuthenticationDetails(
						claims,
						new WebAuthenticationDetailsSource().buildDetails(request)
					);

					authenticationToken.setDetails(details);

					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
