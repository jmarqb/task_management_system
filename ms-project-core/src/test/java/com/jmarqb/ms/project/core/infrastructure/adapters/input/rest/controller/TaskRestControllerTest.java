package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import com.jmarqb.ms.project.core.application.ports.input.TaskUseCase;
import com.jmarqb.ms.project.core.domain.model.Project;
import com.jmarqb.ms.project.core.domain.model.Task;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.TaskResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.mapper.TaskMapper;
import com.jmarqb.ms.project.core.infrastructure.security.CustomAuthenticationDetails;

import org.springframework.http.MediaType;
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
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskRestControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TaskUseCase taskUseCase;
	@Mock
	private TaskMapper taskMapper;

	@InjectMocks
	private TaskRestController taskRestController;

	private ObjectMapper objectMapper;
	private Task task;
	private TaskResponseDto responseDto;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(taskRestController).build();

		task = Instancio.create(Task.class);
		responseDto = Instancio.create(TaskResponseDto.class);

		mockSecurityContextWithUserId(1L);
	}

	void mockSecurityContextWithUserId(Long userId) {
		Claims claims = mock(Claims.class);
		CustomAuthenticationDetails authDetails = mock(CustomAuthenticationDetails.class);
		when(authDetails.claims()).thenReturn(claims);
		when(claims.get(eq("id"), eq(Long.class))).thenReturn(userId);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getDetails()).thenReturn(authDetails);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(context);
	}

	@Test
	void createTask() throws Exception {
		CreateTaskDto createDto = Instancio.create(CreateTaskDto.class);
		createDto.setProjectId(UUID.randomUUID().toString());

		when(taskMapper.toDomain(any(CreateTaskDto.class))).thenReturn(task);
		when(taskUseCase.save(any(Task.class), eq(1L))).thenReturn(task);
		when(taskMapper.toResponse(task, createDto.getProjectId())).thenReturn(responseDto);

		mockMvc.perform(post("/api/v1/tasks")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void searchTasks() throws Exception {
		SearchParamsDto params = new SearchParamsDto();
		params.setPage(0);
		params.setSize(10);
		params.setSort("ASC");

		List<Task> tasks = List.of(task);
		List<TaskResponseDto> responses = List.of(responseDto);

		when(taskUseCase.searchAll(anyInt(), anyInt(), anyString(), eq(1L))).thenReturn(tasks);
		when(taskMapper.toResponseD(any())).thenReturn(responseDto);
		when(taskMapper.toPaginatedResponse(anyList(), anyInt(), anyInt(), anyInt(), any(LocalDateTime.class)))
			.thenReturn(new PaginatedResponseDto());

		mockMvc.perform(get("/api/v1/tasks/search")
			.param("page", String.valueOf(params.getPage()))
			.param("size", String.valueOf(params.getSize()))
			.param("sort", params.getSort()))
			.andExpect(status().isOk());
	}

	@Test
	void findTask() throws Exception {
		String uid = "task-uid";
		Project project = Instancio.create(Project.class);
		task.setProject(project);

		when(taskUseCase.findTaskByUid(uid)).thenReturn(task);
		doNothing().when(taskUseCase).filterTaskUser(task, 1L);
		when(taskMapper.toResponse(task, project.getUid())).thenReturn(responseDto);

		mockMvc.perform(get("/api/v1/tasks/{uid}", uid))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void updateTask() throws Exception {
		String uid = UUID.randomUUID().toString();

		PatchTaskDto patchDto = new PatchTaskDto();
		patchDto.setAssignedUserId(1L);
		patchDto.setStatus("IN_PROGRESS");
		patchDto.setPriority("HIGH");
		patchDto.setName("Tarea de prueba");

		Task task = new Task();
		task.setUid(uid);
		Project project = new Project();
		project.setUid(UUID.randomUUID().toString());
		task.setProject(project);

		TaskResponseDto responseDto = new TaskResponseDto();
		responseDto.setUid(uid);
		responseDto.setStatus("IN_PROGRESS");
		responseDto.setPriority("HIGH");
		responseDto.setName("Tarea de prueba");
		responseDto.setProjectId(project.getUid());

		when(taskMapper.toDomain(any(PatchTaskDto.class))).thenReturn(task);
		when(taskUseCase.updateTask(eq(task), eq(1L))).thenReturn(task);
		when(taskMapper.toResponse(eq(task), eq(project.getUid()))).thenReturn(responseDto);

		mockMvc.perform(patch("/api/v1/tasks/{uid}", uid)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(patchDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.uid").value(uid))
			.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
			.andExpect(jsonPath("$.priority").value("HIGH"))
			.andExpect(jsonPath("$.projectId").value(project.getUid()));
	}

	@Test
	void removeTask() throws Exception {
		String uid = "task-uid";

		DeleteResponseDto response = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();

		mockMvc.perform(delete("/api/v1/tasks/{uid}", uid))
			.andExpect(status().isOk())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.deletedCount").value(response.getDeletedCount()))
			.andExpect(jsonPath("$.acknowledged").value(response.isAcknowledged()));
	}
}