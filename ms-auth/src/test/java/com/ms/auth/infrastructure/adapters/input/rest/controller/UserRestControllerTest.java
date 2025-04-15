package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.auth.application.ports.input.UserUseCase;
import com.ms.auth.application.enums.Gender;
import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.input.rest.advice.HandlerExceptionController;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.UserRole;
import com.ms.auth.infrastructure.adapters.input.rest.mapper.UserMapper;
import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.ms.auth.data.Data.createRole;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(SpringSecurityConfig.class)
class UserRestControllerTest {

	private MockMvc mockMvc;
	private @Mock UserUseCase userUseCase;

	private @Mock UserMapper userMapper;

	private ObjectMapper objectMapper;

	private CreateUserDto createUserDto;

	private CreateUserResponseDto createUserResponseDto;

	private List<User> userList;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(new UserRestController(userUseCase, userMapper))
			.setControllerAdvice(HandlerExceptionController.class)
			.build();

		createUserDto = Instancio.of(CreateUserDto.class)
			.set(field(CreateUserDto::getFirstName), "firstName")
			.set(field(CreateUserDto::getLastName), "lastName")
			.set(field(CreateUserDto::getEmail), "email@email.com")
			.set(field(CreateUserDto::getPassword), "password")
			.set(field(CreateUserDto::getGender), "MALE")
			.set(field(CreateUserDto::getCountry), "country")
			.set(field(CreateUserDto::getPhone), "+1234567890")
			.create();

		createUserResponseDto = Instancio.of(CreateUserResponseDto.class)
			.set(field(CreateUserResponseDto::getId), 1L)
			.set(field(CreateUserResponseDto::getFirstName), createUserDto.getFirstName())
			.set(field(CreateUserResponseDto::getLastName), createUserDto.getLastName())
			.set(field(CreateUserResponseDto::getEmail), createUserDto.getEmail())
			.set(field(CreateUserResponseDto::getGender), Gender.MALE)
			.set(field(CreateUserResponseDto::getCountry), createUserDto.getCountry())
			.set(field(CreateUserResponseDto::getPhone), createUserDto.getPhone())
			.supply(field(CreateUserResponseDto::getRoles), () -> new ArrayList<>())
			.set(field(CreateUserResponseDto::isDeleted), false)
			.set(field(CreateUserResponseDto::getDeletedAt), null)
			.create();
		Role role = createRole(1L);
		createUserResponseDto.getRoles().add(Instancio.of(UserRole.class)
			.set(field(UserRole::getName), role.getName())
			.set(field(UserRole::getDescription), role.getDescription())
			.set(field(UserRole::getIsAdmin), role.isAdmin())
			.set(field(UserRole::getIsDefaultRole), role.isDefaultRole())
			.create());

		userList = List.of(Instancio.of(User.class)
				.set(field(User::getId), createUserResponseDto.getId())
				.set(field(User::getFirstName), createUserResponseDto.getFirstName())
				.set(field(User::getLastName), createUserResponseDto.getLastName())
				.set(field(User::getEmail), createUserResponseDto.getEmail())
				.set(field(User::getGender), createUserDto.getGender())
				.set(field(User::getCountry), createUserResponseDto.getCountry())
				.set(field(User::getPhone), createUserResponseDto.getPhone())
				.set(field(User::getRoles), createUserResponseDto.getRoles())
				.set(field(User::getDeletedAt), null)
				.set(field(User::isDeleted), false)
				.create(),
			Instancio.of(User.class)
				.set(field(User::getId), 2L)
				.set(field(User::getFirstName), "firstName")
				.set(field(User::getLastName), "lastName")
				.set(field(User::getEmail), "email2@email.com")
				.set(field(User::getGender), "FEMALE")
				.set(field(User::getCountry), "country")
				.set(field(User::getPhone), "+1234567898880")
				.set(field(User::getRoles), createUserResponseDto.getRoles())
				.set(field(User::getDeletedAt), null)
				.set(field(User::isDeleted), false)
				.create());
	}

	@Test
	void search() throws Exception {

		SearchBodyDto searchBodyDto = SearchBodyDto.builder()
			.search(null)
			.page(0)
			.size(10)
			.sort("ASC")
			.build();

		PaginatedResponseDto expected = Instancio.of(PaginatedResponseDto.class)
			.set(field(PaginatedResponseDto::getTimestamp), LocalDateTime.now())
			.set(field(PaginatedResponseDto::getTotal), userList.size())
			.set(field(PaginatedResponseDto::getPage), searchBodyDto.getPage())
			.set(field(PaginatedResponseDto::getSize), searchBodyDto.getSize())
			.set(field(PaginatedResponseDto::getData), userList)
			.create();
		when(userUseCase.search(searchBodyDto.getSearch(), searchBodyDto.getPage(), searchBodyDto.getSize(),
			searchBodyDto.getSort())).thenReturn(userList);

		when(userMapper.toResponse(any(User.class))).thenReturn(createUserResponseDto);

		when(userMapper.toPaginatedResponse(any(List.class), anyInt(), anyInt(),
			anyInt(), any(LocalDateTime.class)))
			.thenReturn(expected);


		mockMvc.perform(post("/api/users/search").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(searchBodyDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.total").value(expected.getTotal()))
			.andExpect(jsonPath("$.page").value(expected.getPage()))
			.andExpect(jsonPath("$.size").value(expected.getSize()))
			.andExpect(jsonPath("$.data[0].id").value(createUserResponseDto.getId()))
			.andExpect(jsonPath("$.data[0].firstName").value(createUserResponseDto.getFirstName()))
			.andExpect(jsonPath("$.data[0].lastName").value(createUserResponseDto.getLastName()))
			.andExpect(jsonPath("$.data[0].deleted").value(createUserResponseDto.isDeleted()))
			.andExpect(jsonPath("$.data[0].deletedAt").value(createUserResponseDto.getDeletedAt()));


		verify(userUseCase).search(eq(searchBodyDto.getSearch()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()),
			eq(searchBodyDto.getSort()));

		verify(userMapper).toPaginatedResponse(any(List.class), anyInt(), anyInt(),
			anyInt(), any(LocalDateTime.class));

	}

	@Test
	void findUser() throws Exception {
		when(userUseCase.findUser(createUserResponseDto.getId())).thenReturn(userList.getFirst());
		when(userMapper.toResponse(userList.getFirst())).thenReturn(createUserResponseDto);

		mockMvc.perform(get("/api/users/{id}", createUserResponseDto.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(createUserResponseDto.getId()))
			.andExpect(jsonPath("$.email").value(createUserResponseDto.getEmail()))
			.andExpect(jsonPath("$.firstName").value(createUserResponseDto.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(createUserResponseDto.getLastName()))
			.andExpect(jsonPath("$.deleted").value(createUserResponseDto.isDeleted()));

		verify(userUseCase).findUser(createUserResponseDto.getId());
		verify(userMapper).toResponse(userList.getFirst());
	}

	@Test
	void updateUser() throws Exception {
		UpdateUserDto patchUserDto = UpdateUserDto.builder()
			.firstName(createUserResponseDto.getFirstName())
			.lastName(createUserResponseDto.getLastName())
			.build();


		when(userMapper.toEntity(any(UpdateUserDto.class))).thenReturn(userList.getFirst());
		when(userUseCase.updateUser(any(User.class))).thenReturn(userList.getFirst());
		when(userMapper.toResponse(userList.getFirst())).thenReturn(createUserResponseDto);

		mockMvc.perform(patch("/api/users/{id}", createUserResponseDto.getId())
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(patchUserDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(createUserResponseDto.getId()))
			.andExpect(jsonPath("$.email").value(createUserResponseDto.getEmail()))
			.andExpect(jsonPath("$.firstName").value(createUserResponseDto.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(createUserResponseDto.getLastName()))
			.andExpect(jsonPath("$.deleted").value(createUserResponseDto.isDeleted()));

		verify(userMapper).toEntity(any(UpdateUserDto.class));
		verify(userUseCase).updateUser(any(User.class));
		verify(userMapper).toResponse(any(User.class));
	}

	@Test
	void removeUser() throws Exception {
		DeleteResponseDto response = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();

		mockMvc.perform(delete("/api/users/{id}", createUserResponseDto.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.deletedCount").value(response.getDeletedCount()))
			.andExpect(jsonPath("$.acknowledged").value(response.isAcknowledged()));

		verify(userUseCase).deleteUser(createUserResponseDto.getId());
	}
}