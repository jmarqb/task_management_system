package com.ms.auth.infrastructure.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import com.ms.auth.infrastructure.security.filters.JwtAuthenticationFilter;

@Configuration
public class SpringSecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationConfiguration authenticationConfiguration;

	public SpringSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationConfiguration authenticationConfiguration) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.authenticationConfiguration = authenticationConfiguration;
	}

	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests((authz) -> authz
			.requestMatchers(HttpMethod.POST, "/api/users/search").hasAnyAuthority("USER", "ADMIN")
			.requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyAuthority("USER", "ADMIN")
			.requestMatchers(HttpMethod.PATCH, "/api/users/{id}").hasAuthority("ADMIN")
			.requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority("ADMIN")
			.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
			.requestMatchers("/api/roles/**").hasAuthority("ADMIN")
			.requestMatchers("/swagger-ui/**").permitAll()
			.requestMatchers("/v3/api-docs/**").permitAll()
			.anyRequest().authenticated())
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
			.csrf(config -> config.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
			new CorsFilter(corsConfigurationSource()));
		corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return corsBean;
	}

}
