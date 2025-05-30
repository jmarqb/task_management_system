package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import com.jmarqb.ms.project.core.application.vo.PriorityStatus;
import com.jmarqb.ms.project.core.application.vo.TaskStatus;
import com.jmarqb.ms.project.core.data.seed.TestDataInitializer;
import com.jmarqb.ms.project.core.data.util.Util;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.advice.Error;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchTaskDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.TaskResponseDto;
import com.jmarqb.ms.project.core.infrastructure.security.config.SecurityConfig;
import static com.jmarqb.ms.project.core.data.util.Util.projectUId;
import static com.jmarqb.ms.project.core.data.util.Util.taskNotExist;
import static com.jmarqb.ms.project.core.data.util.Util.taskUid;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SecurityConfig.class, TestDataInitializer.class})
class TaskControllerE2ETest {

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

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		Error error = response.getBody();

		assertThat(error).isNotNull();

		assertThat(error.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(error.getError()).isEqualTo("Bad Request");
		assertThat(error.getMessage()).isEqualTo("Validation failed");
		assertThat(error.getFieldErrors().getFirst().getField()).isEqualTo("name");
		assertThat(error.getFieldErrors().getFirst().getRejectedValue()).isEqualTo("null");
		assertThat(error.getFieldErrors().getFirst().getMessage()).isNotNull();
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

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		PaginatedResponseDto body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getData()).hasSize(2);
		assertThat(body.getPage()).isZero();
		assertThat(body.getSize()).isEqualTo(20);
		assertThat(body.getTotal()).isEqualTo(2);
	}

	@Test
	@Order(4)
	void findTask() {
		responseTaskDto.setAssignedUserId(userId);
		ResponseEntity<TaskResponseDto> response = client.getForEntity(createURI("/api/v1/tasks/" + taskUid),
			TaskResponseDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

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

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		DeleteResponseDto body = response.getBody();

		assertThat(body).isNotNull();
		assertThat(body.getDeletedCount()).isEqualTo(1);
		assertThat(body.isAcknowledged()).isTrue();
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
		assertThat(response.getStatusCode()).isEqualTo(status);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		TaskResponseDto body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(expected).usingRecursiveComparison().ignoringFields("uid").isEqualTo(body);
	}

	public void checkedErrorResponseEntity(ResponseEntity<Error> response, String projectUid, HttpStatus status) {
		assertThat(response.getStatusCode()).isEqualTo(status);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

		Error body = response.getBody();

		assertThat(body).isNotNull();

		assertThat(body.getStatus()).isEqualTo(status.value());
		assertThat(body.getError()).isEqualTo("NOT FOUND");
		assertThat(body.getMessage()).isEqualTo("Task with id " + projectUid + " not found");
	}


	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}


}
