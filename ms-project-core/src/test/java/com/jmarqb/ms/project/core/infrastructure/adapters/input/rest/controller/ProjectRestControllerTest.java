package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import com.jmarqb.ms.project.core.application.exceptions.InvalidProjectForUserException;
import com.jmarqb.ms.project.core.application.exceptions.UnauthorizedProjectException;
import com.jmarqb.ms.project.core.application.ports.input.ProjectUseCase;
import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.ports.output.external.User;
import com.jmarqb.ms.project.core.domain.ports.output.external.UserClient;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.advice.Error;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.advice.HandlerExceptionController;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper.ProjectMapper;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.ValidateUsersDto;
import com.jmarqb.ms.project.core.infrastructure.security.CustomAuthenticationDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectRestControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ProjectUseCase projectUseCase;
	@Mock
	private ProjectMapper projectMapper;
	@Mock
	private UserClient userClient;

	@InjectMocks
	private ProjectRestController projectRestController;

	private ObjectMapper objectMapper;
	private Project project;
	private ProjectResponseDto responseDto;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(projectRestController).build();
		project = Instancio.create(Project.class);
		responseDto = Instancio.create(ProjectResponseDto.class);
	}

	void mockSecurityContextWithUserId(Long userId) {
		Claims claims = mock(Claims.class);
		CustomAuthenticationDetails customAuthDetails = mock(CustomAuthenticationDetails.class);
		when(customAuthDetails.claims()).thenReturn(claims);
		when(claims.get(eq("id"), eq(Long.class))).thenReturn(userId);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getDetails()).thenReturn(customAuthDetails);

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void create() throws Exception {
		mockSecurityContextWithUserId(1L);
		CreateProjectDto createDto = Instancio.of(CreateProjectDto.class)
			.set(field(CreateProjectDto::getName), "New Project")
			.set(field(CreateProjectDto::getOwnerId), null)
			.create();

		when(projectMapper.toDomain(any(CreateProjectDto.class))).thenReturn(project);
		when(projectUseCase.save(project)).thenReturn(project);
		when(projectMapper.toResponse(project)).thenReturn(responseDto);

		mockMvc.perform(post("/api/v1/projects")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.uid").value(responseDto.getUid()));

		verify(projectMapper).toDomain(any(CreateProjectDto.class));
		verify(projectUseCase).save(any(Project.class));
		verify(projectMapper).toResponse(any(Project.class));
	}

	@Test
	void search() throws Exception {
		mockSecurityContextWithUserId(1L);
		SearchParamsDto params = new SearchParamsDto();
		params.setPage(0);
		params.setSize(10);
		params.setSort("ASC");

		List<Project> projectList = List.of(project);
		List<ProjectResponseDto> responseList = List.of(responseDto);

		when(projectUseCase.searchAll(anyInt(), anyInt(), anyString(), anyLong())).thenReturn(projectList);
		when(projectMapper.toResponseListFormatted(anyList(), any(), any())).thenReturn(responseList);
		when(projectMapper.toPaginatedResponse(anyList(), anyInt(), anyInt(), anyInt(), any(LocalDateTime.class)))
			.thenReturn(new PaginatedResponseDto());

		mockMvc.perform(get("/api/v1/projects/search")
			.param("page", String.valueOf(params.getPage()))
			.param("size", String.valueOf(params.getSize()))
			.param("sort", params.getSort())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().isOk());
	}

	@Test
	void findProject() throws Exception {
		String uid = "project-uid";

		when(projectUseCase.findProjectByUid(uid)).thenReturn(project);
		when(projectMapper.toResponseFormatted(any(), any(), any())).thenReturn(responseDto);

		mockMvc.perform(get("/api/v1/projects/{uid}", uid))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void updateProject() throws Exception {
		mockSecurityContextWithUserId(1L);
		String uid = "project-uid";
		PatchProjectDto patchDto = Instancio.create(PatchProjectDto.class);

		when(projectMapper.toDomain(any(PatchProjectDto.class))).thenReturn(project);
		when(projectUseCase.updateProject(any(), anyLong())).thenReturn(project);
		when(projectMapper.toResponse(any())).thenReturn(responseDto);

		mockMvc.perform(patch("/api/v1/projects/{uid}", uid)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(patchDto)))
			.andExpect(status().isOk());
	}

	@Test
	void removeProject() throws Exception {
		mockSecurityContextWithUserId(1L);
		String uid = "project-uid";

		doNothing().when(projectUseCase).deleteProject(eq(uid), anyLong());

		mockMvc.perform(delete("/api/v1/projects/{uid}", uid))
			.andExpect(status().isOk());
	}

	@Test
	void addMembersToProject() throws Exception {
		mockSecurityContextWithUserId(1L);
		String uid = "project-uid";
		ValidateUsersDto dto = Instancio.create(ValidateUsersDto.class);
		List<User> userList = List.of(Instancio.create(User.class));

		when(userClient.checkUsers(anyList())).thenReturn(userList);
		when(projectUseCase.addMembersToProject(anyString(), anyList(), anyLong())).thenReturn(project);
		when(projectMapper.toResponseFormatted(any(), any(), any())).thenReturn(responseDto);

		mockMvc.perform(post("/api/v1/projects/{uid}/members", uid)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk());
	}

	@Test
	void addMembersToProject_ThrowException() throws Exception {
		String uid = "project-uid";
		ValidateUsersDto dto = Instancio.create(ValidateUsersDto.class);
		dto.getUsersIds().clear();

		lenient().when(userClient.checkUsers(anyList()))
			.thenThrow(new IllegalArgumentException("The list of users must not be empty"));

		mockMvc.perform(post("/api/v1/projects/{uid}/members", uid)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isBadRequest());
	}


	@Test
	void removeMemberFromProject() throws Exception {
		mockSecurityContextWithUserId(1L);
		String uid = "project-uid";
		Long memberId = 5L;

		when(projectUseCase.removeMemberFromProject(uid, memberId, 1L)).thenReturn(project);
		when(projectMapper.toResponseFormatted(any(), any(), any())).thenReturn(responseDto);

		mockMvc.perform(delete("/api/v1/projects/{uid}/members/{memberId}", uid, memberId))
			.andExpect(status().isOk());
	}

	@Test
	void createThrowsHttpMessageNotReadableException() throws Exception {
		mockSecurityContextWithUserId(1L);

		CreateProjectDto createDto = Instancio.of(CreateProjectDto.class)
			.set(field(CreateProjectDto::getName), "New Project")
			.set(field(CreateProjectDto::getOwnerId), null)
			.create();
		Project project = projectMapper.toDomain(createDto);

		when(projectUseCase.save(project)).thenThrow(new HttpMessageNotReadableException("Cannot deserialize value for Json"));

		mockMvc.perform(post("/api/v1/projects")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest());
	}

	@Test
	void handleProjectThrowUnauthorized_withMockMvc() throws Exception {
		AccessDeniedException exception = new AccessDeniedException("Unauthorized");

		ResponseEntity<Error> response = new HandlerExceptionController()
			.handleUnauthorizedValidationException(exception);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(401);
		assertThat(response.getBody().getError()).isEqualTo("Unauthorized");
		assertThat(response.getBody().getMessage()).isEqualTo("Unauthorized");
	}

	@Test
	void handleUnauthorizedExceptions() {
		Exception exception = new UnauthorizedProjectException("You are not allowed");

		ResponseEntity<Error> response = new HandlerExceptionController()
			.handleUnauthorizedExceptions(exception);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(401);
		assertThat(response.getBody().getError()).isEqualTo("Unauthorized Access");
		assertThat(response.getBody().getMessage()).isEqualTo("You are not allowed");
	}

	@Test
	void InvalidProjectForUserException_BadRequest() {
		Exception exception = new InvalidProjectForUserException("Project is archived");

		ResponseEntity<Error> response = new HandlerExceptionController()
			.handleInvalidProjectForUserException(exception);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(400);
		assertThat(response.getBody().getError()).isEqualTo("Invalid Project for User");
		assertThat(response.getBody().getMessage()).isEqualTo("Project is archived");
	}

	@Test
	void handleValidationException_BadRequest() {
		HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Malformed JSON");

		ResponseEntity<Error> response = new HandlerExceptionController()
			.handleValidationException(exception, null);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(400);
		assertThat(response.getBody().getError()).isEqualTo("Json Error");
		assertThat(response.getBody().getMessage()).isEqualTo("Malformed JSON");
	}
}