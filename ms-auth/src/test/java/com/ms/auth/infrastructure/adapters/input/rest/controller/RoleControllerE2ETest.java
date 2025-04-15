package com.ms.auth.infrastructure.adapters.input.rest.controller;

import com.ms.auth.domain.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.auth.application.ports.input.JwtUseCase;
import com.ms.auth.data.seed.TestDataInitializer;
import com.ms.auth.domain.model.Error;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.RoleToUsersDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateRoleResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static com.ms.auth.data.Data.createRoleUser;
import static com.ms.auth.data.Data.createSearchBodyDto;
import static com.ms.auth.data.Data.getRoleAdmin;
import static com.ms.auth.data.Data.getRoleUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
public class RoleControllerE2ETest {

	@Autowired
	private TestRestTemplate client;

	@Autowired
	private JwtUseCase jwtService;

	@Autowired
	private UserRepository userRepository;
	private String token;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() throws JsonProcessingException {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ADMIN"));
		CustomUserDetails user = new CustomUserDetails(
			1L,
			"testadmin@example.com",
			"password",
			true,
			true,
			true,
			true,
			authorities
		);
		token = configureJwtToken(user);
		client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().set("Authorization", "Bearer " + token);
			return execution.execute(request, body);
		});
	}

	protected String configureJwtToken(CustomUserDetails user) throws JsonProcessingException {
		return jwtService.generateToken(user);
	}


	@Test
	@Order(1)
	void create() {
		CreateRoleDto createRoleDto = createRoleUser();
		createRoleDto.setName("USER");
		CreateRoleResponseDto expected = getRoleUser(3L);
		expected.setName(createRoleDto.getName());

		ResponseEntity<CreateRoleResponseDto> response = client.postForEntity(createURI("/api/roles"), createRoleDto,
			CreateRoleResponseDto.class);
		checkedResponseEntity(response, expected, HttpStatus.CREATED);
	}

	@Test
	@Order(2)
	void createThrowBadRequest() {
		CreateRoleDto createRoleDto = createRoleUser();
		createRoleDto.setName(null);

		ResponseEntity<Error> response = client.postForEntity(createURI("/api/roles"), createRoleDto,
			Error.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		Error error = response.getBody();

		assertNotNull(error);

		assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
		assertEquals("Bad Request", error.getError());
		assertEquals("Validation failed", error.getMessage());
		assertEquals("name", error.getFieldErrors().getFirst().getField());
		assertEquals("null", error.getFieldErrors().getFirst().getRejectedValue());
		assertEquals("name is required", error.getFieldErrors().getFirst().getMessage());

	}

	@Test
	@Order(3)
	void search() {
		SearchBodyDto searchBodyDto = createSearchBodyDto("ad", 0, 10, "ASC");

		ResponseEntity<PaginatedResponseDto> response = client.postForEntity(createURI("/api/roles/search"),
			searchBodyDto, PaginatedResponseDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		PaginatedResponseDto body = response.getBody();

		assertNotNull(body);
		assertEquals(1, body.getData().size());
		assertEquals(0, body.getPage());
		assertEquals(10, body.getSize());
		assertEquals(1, body.getTotal());
	}

	@Test
	@Order(4)
	void findRole() {
		CreateRoleResponseDto expected = getRoleUser(3L);
		ResponseEntity<CreateRoleResponseDto> response = client.getForEntity(createURI("/api/roles/%d".formatted((3L))),
			CreateRoleResponseDto.class);

		checkedResponseEntity(response, expected, HttpStatus.OK);
	}

	@Test
	@Order(5)
	void findRoleIfNotExist() {

		ResponseEntity<Error> response = client.getForEntity(createURI("/api/roles/10000"),
			Error.class);

		checkedErrorResponseEntity(response, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(6)
	void updateRole() {
		CreateRoleResponseDto roleAdmin = getRoleAdmin(3L);

		UpdateRoleDto updateRoleDto = UpdateRoleDto.builder()
			.name(roleAdmin.getName())
			.description(roleAdmin.getDescription())
			.icon(roleAdmin.getIcon())
			.isAdmin(roleAdmin.getIsAdmin())
			.isDefaultRole(roleAdmin.getIsDefaultRole())
			.build();

		HttpEntity<UpdateRoleDto> entity = new HttpEntity<>(updateRoleDto);


		ResponseEntity<CreateRoleResponseDto> response = client.exchange(
			createURI("/api/roles/%d".formatted(3L)),
			PATCH,
			entity,
			CreateRoleResponseDto.class
		);

		checkedResponseEntity(response, roleAdmin, HttpStatus.OK);
	}

	@Test
	@Order(7)
	void updateRoleIfNotExist() {
		CreateRoleResponseDto roleAdmin = getRoleAdmin(3L);

		UpdateRoleDto updateRoleDto = UpdateRoleDto.builder()
			.name(roleAdmin.getName())
			.description(roleAdmin.getDescription())
			.icon(roleAdmin.getIcon())
			.isAdmin(roleAdmin.getIsAdmin())
			.isDefaultRole(roleAdmin.getIsDefaultRole())
			.build();

		ResponseEntity<Error> response = client.exchange(
			createURI("/api/roles/10000"),
			PATCH,
			new HttpEntity<>(updateRoleDto),
			Error.class
		);

		checkedErrorResponseEntity(response, HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(8)
	void removeRole() {

		ResponseEntity<DeleteResponseDto> response = client.exchange(
			createURI("/api/roles/%d".formatted(3L)),
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

		ResponseEntity<Error> findRoleAfterDelete = client.getForEntity(
			createURI("/api/roles/%d".formatted(3L)),
			Error.class);
		checkedErrorResponseEntity(findRoleAfterDelete, HttpStatus.NOT_FOUND);

	}

	@Test
	@Order(9)
	void addRoleToManyUsers() {
		//Create a new Role for adding to a existing user
		CreateRoleDto createRoleDto = createRoleUser();
		createRoleDto.setName("Test_role");
		CreateRoleResponseDto expected = getRoleUser(4L);
		expected.setName(createRoleDto.getName());
		ResponseEntity<CreateRoleResponseDto> responseRole = client.postForEntity(createURI("/api/roles"), createRoleDto,
			CreateRoleResponseDto.class);

		checkedResponseEntity(responseRole, expected, HttpStatus.CREATED);

		//Add role to user
		RoleToUsersDto roleToUsersDto = new RoleToUsersDto();
		roleToUsersDto.setRoleId(responseRole.getBody().getId()); //id 4
		roleToUsersDto.setUsersId(new Long[]{1L});


		ResponseEntity<PaginatedResponseDto> response = client.postForEntity(createURI("/api/roles/add/to-many-users"), roleToUsersDto,
			PaginatedResponseDto.class);


		PaginatedResponseDto body = response.getBody();

		assertNotNull(body);
		UserEntity user = userRepository.findByEmailWithRoles("testadmin@example.com").orElseThrow();
		assertEquals(3, user.getRoles().size());
		assertEquals(0, body.getPage());
		assertEquals(1, body.getSize());
		assertEquals(1, body.getTotal());
	}

	@Test
	@Order(10)
	void addRoleToManyUsersThrowDuplicateKeyException() {
		CreateRoleResponseDto role = getRoleUser(4L);

		RoleToUsersDto roleToUsersDto = new RoleToUsersDto();
		roleToUsersDto.setRoleId(role.getId());
		roleToUsersDto.setUsersId(new Long[]{1L});


		ResponseEntity<Error> response = client.postForEntity(createURI("/api/roles/add/to-many-users"), roleToUsersDto,
			Error.class);


		Error body = response.getBody();

		assertNotNull(body);

		assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
		assertEquals("Duplicate Key", body.getError());
		assertEquals("Could not execute statement: Duplicate key or Duplicate entry", body.getMessage());
	}

	@Test
	@Order(11)
	void removeRoleToManyUsers() {

		CreateRoleResponseDto role = getRoleUser(4L);

		RoleToUsersDto roleToUsersDto = new RoleToUsersDto();
		roleToUsersDto.setRoleId(role.getId());
		roleToUsersDto.setUsersId(new Long[]{1L});

		ResponseEntity<PaginatedResponseDto> response = client.exchange(
			createURI("/api/roles/remove/to-many-users"),
			DELETE,
			new HttpEntity<>(roleToUsersDto),
			PaginatedResponseDto.class
		);

		PaginatedResponseDto body = response.getBody();
		assertNotNull(body);

		UserEntity user = userRepository.findByEmailWithRoles("testadmin@example.com").orElseThrow();

		assertEquals(2, user.getRoles().size());
		assertEquals(0, body.getPage());
		assertEquals(1, body.getSize());
		assertEquals(1, body.getTotal());
	}

	private void checkedResponseEntity(ResponseEntity<CreateRoleResponseDto> response, CreateRoleResponseDto expected,
																		 HttpStatus status) {
		assertEquals(status, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		CreateRoleResponseDto body = response.getBody();
		assertNotNull(body);
		assertEquals(expected.getName(), body.getName());
		assertEquals(expected.getDescription(), body.getDescription());
		assertEquals(expected.getIcon(), body.getIcon());
		assertEquals(expected.isDeleted(), body.isDeleted());
		assertEquals(expected.getDeletedAt(), body.getDeletedAt());
		assertEquals(expected.getId(), body.getId());
	}

	public void checkedErrorResponseEntity(ResponseEntity<Error> response, HttpStatus status) {
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

		Error body = response.getBody();

		assertNotNull(body);

		assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
		assertEquals("NOT FOUND", body.getError());
		assertEquals("The role does not exist", body.getMessage());
	}

	private String createURI(String uri) {
		return "http://localhost:" + port + uri;
	}


}
