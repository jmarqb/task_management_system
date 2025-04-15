package com.ms.auth.application.service.impl;

import com.ms.auth.application.impl.AuthUseCaseImpl;
import com.ms.auth.domain.model.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.application.ports.input.JwtUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

	private @Mock AuthenticationManager authenticationManager;
	private @Mock JwtUseCase jwtUseCase;
	private @InjectMocks AuthUseCaseImpl authUseCaseImpl;

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
		when(jwtUseCase.generateToken(mockUser)).thenReturn(mockToken);

		String response = authUseCaseImpl.login(email, password);

		assertThat(response).isNotNull();
		assertEquals(mockToken, response);

		verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
			email, password
		));
		verify(jwtUseCase).generateToken(mockUser);
	}

	@Test
	void loginThrowBadCredentialsException() {
		String email = "invalid@test.com";
		String password = "wrong_password";

		when(authenticationManager.authenticate(any(Authentication.class)))
			.thenThrow(new BadCredentialsException("Invalid email or password"));

		assertThatExceptionOfType(BadCredentialsException.class).isThrownBy(() -> authUseCaseImpl.login(email, password));

		verify(authenticationManager).authenticate(any(Authentication.class));
		verifyNoInteractions(jwtUseCase);
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

		when(jwtUseCase.generateToken(mockUser)).thenThrow(new JsonProcessingException("Error generating token") {
		});

		RuntimeException exception = assertThrows(RuntimeException.class, () -> authUseCaseImpl.login(email, password));
		assertEquals("Error generating token", exception.getCause().getMessage());


		verify(authenticationManager).authenticate(any(Authentication.class));
		verify(jwtUseCase).generateToken(mockUser);
	}

}