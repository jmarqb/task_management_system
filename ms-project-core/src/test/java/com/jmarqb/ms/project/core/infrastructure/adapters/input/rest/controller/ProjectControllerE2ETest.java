package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.springframework.web.client.HttpClientErrorException;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.project.core.data.seed.TestDataInitializer;
import com.jmarqb.ms.project.core.data.util.Util;
import com.jmarqb.ms.project.core.domain.model.Error;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.CreateProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.PatchProjectDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.request.SearchParamsDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response.ProjectResponseDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.UserServiceFeignClient;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.UserDto;
import com.jmarqb.ms.project.core.infrastructure.adapters.output.external.dtos.ValidateUsersDto;
import com.jmarqb.ms.project.core.infrastructure.security.config.SecurityConfig;
import feign.FeignException;
import feign.Request;
import feign.Response;
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
import static com.jmarqb.ms.project.core.data.util.Util.projectNotExist;
import static com.jmarqb.ms.project.core.data.util.Util.projectUId;
import static com.jmarqb.ms.project.core.data.util.Util.userMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SecurityConfig.class, TestDataInitializer.class})
public class ProjectControllerE2ETest {

	@Autowired
	private TestRestTemplate client;

	@MockBean
	private UserServiceFeignClient userServiceFeignClient;

	private String mockToken;

	private Claims claims;

	private Long userId;
	private ProjectResponseDto responseDto;

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

		responseDto = Instancio.of(ProjectResponseDto.class)
			.set(field(ProjectResponseDto::getUid), UUID.randomUUID().toString())
			.set(field(ProjectResponseDto::getName), "new-name")
			.set(field(ProjectResponseDto::getDescription), "new description")
			.set(field(ProjectResponseDto::getOwnerId), userId)
			.set(field(ProjectResponseDto::getMembers), null)
			.set(field(ProjectResponseDto::getTasks), null)
			.set(field(ProjectResponseDto::isDeleted), false)
			.set(field(ProjectResponseDto::getDeletedAt), null)
			.set(field(ProjectResponseDto::isArchived), false)
			.create();
	}

	@Test
	@Order(1)
	void create() {
		CreateProjectDto createProjectDto = CreateProjectDto.builder()
			.name(responseDto.getName())
			.description(responseDto.getDescription())
			.build();

		ResponseEntity<ProjectResponseDto> response = client.postForEntity(createURI("/api/v1/projects"),
			createProjectDto, ProjectResponseDto.class);

		checkedResponseEntity(response, responseDto, HttpStatus.CREATED);
	}

	@Test
	@Order(2)
	void createThrowBadRequest() {
		CreateProjectDto createProjectDto = CreateProjectDto.builder()
			.name(null)
			.description(responseDto.getDescription())
			.build();

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/v1/projects"),
			createProjectDto, Error.class);

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
			createURI("/api/v1/projects/search"),
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
	void findProject() {
		ResponseEntity<ProjectResponseDto> response = client.getForEntity(createURI("/api/v1/projects/" + projectUId),
			ProjectResponseDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		ProjectResponseDto body = response.getBody();
		assertNotNull(body);
		assertEquals(projectUId, body.getUid());
		assertEquals("Test Project", body.getName());
		assertEquals("Test Project Description", body.getDescription());
		assertEquals(userId, body.getOwnerId());
	}

	@Test
	@Order(5)
	void findProjectIfNotExist() {
		ResponseEntity<Error> response = client.getForEntity(createURI("/api/v1/projects/" +
			projectNotExist),
			Error.class);

		checkedErrorResponseEntity(response, projectNotExist, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(6)
	void updateProject() {
		responseDto.setName("UpdatedTestProject");
		responseDto.setDescription("UpdatedTestProjectDescription");
		PatchProjectDto patchProjectDto = PatchProjectDto.builder()
			.name("UpdatedTestProject")
			.description("UpdatedTestProjectDescription")
			.isArchived(true)
			.build();

		HttpEntity<PatchProjectDto> entity = new HttpEntity<>(patchProjectDto);

		ResponseEntity<ProjectResponseDto> response = client.exchange(createURI("/api/v1/projects/" +
			projectUId),
			PATCH,
			entity,
			ProjectResponseDto.class);
		assertThat(responseDto).usingRecursiveComparison().ignoringFields("uid", "members", "tasks")
			.isEqualTo(response.getBody());
	}

	@Test
	@Order(7)
	void updateProjectIfNotExists() {
		PatchProjectDto patchProjectDto = PatchProjectDto.builder()
			.name("UpdatedTestProject")
			.description("UpdatedTestProjectDescription")
			.build();

		HttpEntity<PatchProjectDto> entity = new HttpEntity<>(patchProjectDto);

		ResponseEntity<Error> response = client.exchange(createURI("/api/v1/projects/" +
			projectNotExist),
			PATCH,
			entity,
			Error.class);
		checkedErrorResponseEntity(response, projectNotExist, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(8)
	void AddMembers() {
		Set<UserDto> userListDto = Set.of(Instancio.create(UserDto.class));
		userListDto.forEach(user -> user.setId(userMemberId));

		List<Long> userIds = userListDto.stream().map(UserDto::getId).toList();
		ValidateUsersDto dto = ValidateUsersDto.builder().usersIds(userIds).build();

		when(userServiceFeignClient.checkUsersIds(any(ValidateUsersDto.class)))
			.thenReturn(userListDto);

		ResponseEntity<ProjectResponseDto> response = client.postForEntity(createURI("/api/v1/projects/"
			+ projectUId + "/members"), dto, ProjectResponseDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	@Order(9)
	void handleFeignException() {
		Set<UserDto> userListDto = Set.of(Instancio.create(UserDto.class));
		userListDto.forEach(user -> user.setId(userMemberId));

		List<Long> userIds = userListDto.stream().map(UserDto::getId).toList();
		ValidateUsersDto dto = ValidateUsersDto.builder().usersIds(userIds).build();

		FeignException feignException = FeignException.errorStatus("checkUsersIds", Response.builder()
			.status(HttpStatus.BAD_GATEWAY.value())
			.reason("Error calling external service")
			.request(Request.create(Request.HttpMethod.POST, "/users/validate", new HashMap<>(), null,
				Charset.defaultCharset()))
			.build());

		when(userServiceFeignClient.checkUsersIds(any(ValidateUsersDto.class)))
			.thenThrow(feignException);

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/v1/projects/"
			+ projectUId + "/members"), dto, Error.class);

		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());

		Error error = response.getBody();

		assertNotNull(error);
		assertEquals(HttpStatus.BAD_GATEWAY.value(), error.getStatus());
		assertEquals("Bad Gateway", error.getError());
		assertEquals("Unknown error from external service", error.getMessage());
	}

	@Test
	@Order(10)
	void removeMember() {
		ResponseEntity<ProjectResponseDto> response = client.exchange(
			createURI("/api/v1/projects/" + projectUId + "/members/" + userMemberId),
			DELETE,
			new HttpEntity<>(null),
			ProjectResponseDto.class
		);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

	}

	@Test
	@Order(11)
	void deleteProject() {
		ResponseEntity<DeleteResponseDto> response = client.exchange(
			createURI("/api/v1/projects/" + projectUId),
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
	@Order(12)
	void deleteProjectIfNotExists() {
		ResponseEntity<Error> response = client.exchange(
			createURI("/api/v1/projects/" + projectNotExist),
			DELETE,
			new HttpEntity<>(null),
			Error.class
		);

		checkedErrorResponseEntity(response, projectNotExist, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(13)
	void handleHttpClientErrorException_Unauthorized() {
		ValidateUsersDto dto = ValidateUsersDto.builder().usersIds(List.of(userMemberId)).build();

		when(userServiceFeignClient.checkUsersIds(any(ValidateUsersDto.class)))
			.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/v1/projects/" + projectUId +
			"/members"), dto, Error.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Unauthorized", response.getBody().getError());
		assertEquals("Unauthorized", response.getBody().getMessage());
	}

	private void checkedResponseEntity(ResponseEntity<ProjectResponseDto> response, ProjectResponseDto expected,
																		 HttpStatus status) {
		assertEquals(status, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		ProjectResponseDto body = response.getBody();
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
		assertEquals("Project with id " + projectUid + " not found", body.getMessage());
	}

	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}

}
