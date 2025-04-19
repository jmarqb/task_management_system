package com.ms.auth.infrastructure.security.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ms.auth.application.impl.JpaUserDetailsUseCaseImpl;
import com.ms.auth.application.ports.input.JwtUseCase;
import com.ms.auth.domain.model.CustomUserDetails;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUseCase jwtUseCase;
	private final JpaUserDetailsUseCaseImpl userDetailsService;

	public JwtAuthenticationFilter(JwtUseCase jwtService, JpaUserDetailsUseCaseImpl userDetailsService) {
		this.jwtUseCase = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String HEADER_AUTHORIZATION = "Authorization";

		String PREFIX_TOKEN = "Bearer ";


		String header = request.getHeader(HEADER_AUTHORIZATION);

		if (header != null && header.startsWith(PREFIX_TOKEN)) {

			String token = header.replace(PREFIX_TOKEN, "");

			String username = jwtUseCase.extractUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (jwtUseCase.isTokenValid(token, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authenticationToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}

		}
		filterChain.doFilter(request, response);
	}
}
