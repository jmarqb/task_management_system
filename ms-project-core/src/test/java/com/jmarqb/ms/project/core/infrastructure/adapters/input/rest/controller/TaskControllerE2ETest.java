package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.project.core.application.enums.PriorityStatus;
import com.jmarqb.ms.project.core.application.enums.TaskStatus;
import com.jmarqb.ms.project.core.data.seed.TestDataInitializer;
import com.jmarqb.ms.project.core.data.util.Util;
import com.jmarqb.ms.project.core.domain.model.Error;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.TaskResponseDto;
import com.jmarqb.ms.project.core.infrastructure.security.config.SecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static com.jmarqb.ms.project.core.data.util.Util.projectUId;
import static com.jmarqb.ms.project.core.data.util.Util.taskNotExist;
import static com.jmarqb.ms.project.core.data.util.Util.taskUid;
import static com.jmarqb.ms.project.core.data.util.Util.userId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SecurityConfig.class, TestDataInitializer.class})
public class TaskControllerE2ETest {

	@Autowired
	private TestRestTemplate client;

	private String mockToken;

	private Claims claims;

	private Long userId;
	private TaskResponseDto responseTaskDto;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() throws JsonProcessingException {
		String TEST_SECRET = "M1_Testing_secret_ECOMMERCE_SECRET_KEY";
		SecretKey secret = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));

		List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("USER"),
			new SimpleGrantedAuthority("ADMIN"));

		String username = "testUser";
		userId = Util.userId;

		claims = Jwts.claims()
			.add("authorities", new ObjectMapper().writeValueAsString(roles))
			.add("username", username)
			.add("id", userId)
			.add("iss", "ms-auth")
			.build();

		mockToken = Jwts.builder()
			.subject(username)
			.issuer("ms-auth")
			.claims(claims)
			.expiration(new Date(System.currentTimeMillis() + 1200000))
			.issuedAt(new Date())
			.signWith(secret)
			.compact();


		client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().set("Authorization", "Bearer " + mockToken);
			return execution.execute(request, body);
		});

		responseTaskDto = Instancio.of(TaskResponseDto.class)
			.set(field(TaskResponseDto::getUid), taskUid)
			.set(field(TaskResponseDto::getName), "Test Task")
			.set(field(TaskResponseDto::getStatus), TaskStatus.PENDING.toString())
			.set(field(TaskResponseDto::getPriority), PriorityStatus.MEDIUM.toString())
			.set(field(TaskResponseDto::getProjectId), projectUId)
			.set(field(TaskResponseDto::getAssignedUserId), userId)
			.set(field(TaskResponseDto::isDeleted), false)
			.set(field(TaskResponseDto::getDeletedAt), null)
			.create();
	}

	@Test
	@Order(1)
	void create() {
		CreateTaskDto createTaskDto = CreateTaskDto.builder()
			.name(responseTaskDto.getName())
			.projectId(responseTaskDto.getProjectId())
			.assignedUserId(responseTaskDto.getAssignedUserId())
			.priority(responseTaskDto.getPriority())
			.status(responseTaskDto.getStatus())
			.build();

		ResponseEntity<TaskResponseDto> response = client.postForEntity(createURI("/api/v1/tasks"),
			createTaskDto, TaskResponseDto.class);

		checkedResponseEntity(response, responseTaskDto, HttpStatus.CREATED);
	}

	@Test
	@Order(2)
	void createThrowBadRequest() {
		CreateTaskDto createTaskDto = CreateTaskDto.builder()
			.name(null)
			.projectId(responseTaskDto.getProjectId())
			.assignedUserId(responseTaskDto.getAssignedUserId())
			.priority(responseTaskDto.getPriority())
			.status(responseTaskDto.getStatus())
			.build();

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/v1/tasks"),
			createTaskDto, Error.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		Error error = response.getBody();

		assertNotNull(error);

		assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
		assertEquals("Bad Request", error.getError());
		assertEquals("Validation failed", error.getMessage());
		assertEquals("name", error.getFieldErrors().getFirst().getField());
		assertEquals("null", error.getFieldErrors().getFirst().getRejectedValue());
		assertNotNull(error.getFieldErrors().getFirst().getMessage());
	}

	@Test
	@Order(3)
	void search() {
		SearchParamsDto params = new SearchParamsDto();
		params.setPage(0);
		params.setSize(10);
		params.setSort("ASC");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SearchParamsDto> entity = new HttpEntity<>(params, headers);
		ResponseEntity<PaginatedResponseDto> response = client.exchange(
			createURI("/api/v1/tasks/search"),
			GET,
			entity,
			PaginatedResponseDto.class
		);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		PaginatedResponseDto body = response.getBody();
		assertNotNull(body);
		assertEquals(2, body.getData().size());
		assertEquals(0, body.getPage());
		assertEquals(20, body.getSize());
		assertEquals(2, body.getTotal());
	}

	@Test
	@Order(4)
	void findTask() {
		responseTaskDto.setAssignedUserId(userId);
		ResponseEntity<TaskResponseDto> response = client.getForEntity(createURI("/api/v1/tasks/" + taskUid),
			TaskResponseDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		checkedResponseEntity(response, responseTaskDto, HttpStatus.OK);
	}

	@Test
	@Order(5)
	void findTaskIfNotExists() {

		ResponseEntity<Error> response = client.getForEntity(createURI("/api/v1/tasks/" +
			taskNotExist),
			Error.class);

		checkedErrorResponseEntity(response, taskNotExist, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(6)
	void updateTask() {
		responseTaskDto.setAssignedUserId(userId);
		responseTaskDto.setName("UpdatedTestTask");
		responseTaskDto.setStatus(TaskStatus.IN_PROGRESS.toString());
		responseTaskDto.setPriority(PriorityStatus.HIGH.toString());

		PatchTaskDto patchTaskDto = PatchTaskDto.builder()
			.name("UpdatedTestTask")
			.status(TaskStatus.IN_PROGRESS.toString())
			.priority(PriorityStatus.HIGH.toString())
			.assignedUserId(userId)
			.build();

		HttpEntity<PatchTaskDto> entity = new HttpEntity<>(patchTaskDto);

		ResponseEntity<TaskResponseDto> response = client.exchange(createURI("/api/v1/tasks/" +
			taskUid),
			PATCH,
			entity,
			TaskResponseDto.class);
		checkedResponseEntity(response, responseTaskDto, HttpStatus.OK);
	}

	@Test
	@Order(7)
	void updateTaskIfNotExists() {
		PatchTaskDto patchTaskDto = PatchTaskDto.builder()
			.name("UpdatedTestTask")
			.status(TaskStatus.IN_PROGRESS.toString())
			.priority(PriorityStatus.HIGH.toString())
			.assignedUserId(userId)
			.build();

		HttpEntity<PatchTaskDto> entity = new HttpEntity<>(patchTaskDto);

		ResponseEntity<Error> response = client.exchange(createURI("/api/v1/tasks/" +
			taskNotExist),
			PATCH,
			entity,
			Error.class);
		checkedErrorResponseEntity(response, taskNotExist, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(10)
	void deleteTask() {
		ResponseEntity<DeleteResponseDto> response = client.exchange(
			createURI("/api/v1/tasks/" + taskUid),
			DELETE,
			new HttpEntity<>(null),
			DeleteResponseDto.class
		);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		DeleteResponseDto body = response.getBody();

		assertNotNull(body);
		assertEquals(1, body.getDeletedCount());
		assertTrue(body.isAcknowledged());
	}

	@Test
	@Order(11)
	void deleteTaskIfNotExists() {
		ResponseEntity<Error> response = client.exchange(
			createURI("/api/v1/tasks/" + taskNotExist),
			DELETE,
			new HttpEntity<>(null),
			Error.class
		);

		checkedErrorResponseEntity(response, taskNotExist, HttpStatus.NOT_FOUND);
	}

	private void checkedResponseEntity(ResponseEntity<TaskResponseDto> response, TaskResponseDto expected,
																		 HttpStatus status) {
		assertEquals(status, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		TaskResponseDto body = response.getBody();
		assertNotNull(body);
		assertThat(expected).usingRecursiveComparison().ignoringFields("uid").isEqualTo(body);
	}

	public void checkedErrorResponseEntity(ResponseEntity<Error> response, String projectUid, HttpStatus status) {
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		Error body = response.getBody();

		assertNotNull(body);

		assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
		assertEquals("NOT FOUND", body.getError());
		assertEquals("Task with id " + projectUid + " not found", body.getMessage());
	}


	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}


}
