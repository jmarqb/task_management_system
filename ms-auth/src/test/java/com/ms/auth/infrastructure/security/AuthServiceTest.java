package com.ms.auth.infrastructure.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.infrastructure.security.model.CustomUserDetails;
import com.ms.auth.infrastructure.security.service.AuthService;
import com.ms.auth.infrastructure.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	private @Mock AuthenticationManager authenticationManager;
	private @Mock JwtService jwtUseService;
	private @InjectMocks AuthService authService;

	@Test
	void loginValidCredentials() throws JsonProcessingException {
		String email = "admin@test.com";
		String password = "1234";

		CustomUserDetails mockUser = mock(CustomUserDetails.class);
		Authentication mockAuthentication = mock(Authentication.class);

		when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
			email, password
		))).thenReturn(mockAuthentication);

		String mockToken = "eyJhbGciOiJIUzI1NiJ9.mock.jwt.token";
		when(jwtUseService.generateToken(mockUser)).thenReturn(mockToken);

		String response = authService.login(email, password);

		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(mockToken);

		verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
			email, password
		));
		verify(jwtUseService).generateToken(mockUser);
	}

	@Test
	void loginThrowBadCredentialsException() {
		String email = "invalid@test.com";
		String password = "wrong_password";

		when(authenticationManager.authenticate(any(Authentication.class)))
			.thenThrow(new BadCredentialsException("Invalid email or password"));

		assertThatExceptionOfType(BadCredentialsException.class).isThrownBy(() -> authService.login(email, password));

		verify(authenticationManager).authenticate(any(Authentication.class));
		verifyNoInteractions(jwtUseService);
	}

	@Test
	void loginThrowJsonProcessingException() throws JsonProcessingException {
		String email = "admin@test.com";
		String password = "1234";

		CustomUserDetails mockUser = mock(CustomUserDetails.class);
		Authentication mockAuthentication = mock(Authentication.class);

		when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
			email, password
		))).thenReturn(mockAuthentication);

		when(jwtUseService.generateToken(mockUser)).thenThrow(new JsonProcessingException("Error generating token") {
		});

		RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(email, password));
		assertThat(exception.getCause().getMessage()).isEqualTo("Error generating token");


		verify(authenticationManager).authenticate(any(Authentication.class));
		verify(jwtUseService).generateToken(mockUser);
	}

}