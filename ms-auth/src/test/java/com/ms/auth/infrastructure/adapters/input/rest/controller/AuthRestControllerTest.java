package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.auth.application.ports.input.AuthUseCase;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.LoginDto;
import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Import(SpringSecurityConfig.class)
class AuthRestControllerTest {

	private MockMvc mockMvc;

	private @Mock AuthUseCase authUseCase;

	@Test
	void login() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(new AuthRestController(authUseCase)).build();
		String mockToken = "eyJhbGciOiJIUzI1NiJ9.mock.jwt.token";

		String email = "testuser@example.com";
		String password = "testpassword";

		when(authUseCase.login(email, password)).thenReturn(mockToken);


		mockMvc.perform(post("/api/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(new ObjectMapper().writeValueAsString(new LoginDto(email, password)))
		)
		.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.token").value(mockToken));

		verify(authUseCase).login(email, password);
	}
}