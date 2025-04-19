package com.ms.auth.infrastructure.adapters.input.rest.controller;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import com.ms.auth.application.ports.input.RoleUseCase;
import com.ms.auth.domain.model.Role;
import com.ms.auth.infrastructure.adapters.input.rest.advice.HandlerExceptionController;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.UpdateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateRoleResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.DeleteResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.PaginatedResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.mapper.RoleMapper;
import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(SpringSecurityConfig.class)
class RoleRestControllerTest {

	private MockMvc mockMvc;
	private @Mock RoleUseCase roleUseCase;

	private @Mock RoleMapper roleMapper;

	private ObjectMapper objectMapper;

	private CreateRoleResponseDto createRoleResponseDto;

	private CreateRoleDto createRoleDto;

	private List<Role> rolesList;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(new RoleRestController(roleUseCase, roleMapper))
			.setControllerAdvice(HandlerExceptionController.class)
			.build();

		createRoleResponseDto = Instancio.of(CreateRoleResponseDto.class)
			.set(field(CreateRoleResponseDto::getId), 1L)
			.set(field(CreateRoleResponseDto::getName), "name")
			.set(field(CreateRoleResponseDto::getDescription), "description")
			.set(field(CreateRoleResponseDto::isDeleted), false)
			.set(field(CreateRoleResponseDto::getDeletedAt), null)
			.create();

		createRoleDto = Instancio.of(CreateRoleDto.class)
			.set(field(CreateRoleDto::getName), "name")
			.set(field(CreateRoleDto::getDescription), "description")
			.set(field(CreateRoleDto::getIsAdmin), false)
			.set(field(CreateRoleDto::getIsDefaultRole), true)
			.create();

		rolesList = List.of(Instancio.of(Role.class)
				.set(field(Role::getId), createRoleResponseDto.getId())
				.set(field(Role::getName), createRoleResponseDto.getName())
				.set(field(Role::getDescription), createRoleResponseDto.getDescription())
				.set(field(Role::isDeleted), createRoleResponseDto.isDeleted())
				.set(field(Role::getDeletedAt), createRoleResponseDto.getDeletedAt())
				.set(field(Role::isAdmin), createRoleDto.getIsAdmin())
				.set(field(Role::isDefaultRole), createRoleDto.getIsDefaultRole())
				.create(),
			Instancio.of(Role.class)
				.set(field(Role::getId), 2L)
				.set(field(Role::getName), "name2")
				.set(field(Role::getDescription), "description2")
				.set(field(Role::isDeleted), false)
				.set(field(Role::getDeletedAt), null)
				.set(field(Role::isAdmin), false)
				.set(field(Role::isDefaultRole), false)
				.create());
	}

	@Test
	void create() throws Exception {

		Role role = roleMapper.toEntity(createRoleDto);

		when(roleUseCase.save(role)).thenReturn(role);
		when(roleMapper.toResponse(role)).thenReturn(createRoleResponseDto);

		mockMvc.perform(post("/api/roles")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createRoleDto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(createRoleResponseDto.getId()))
			.andExpect(jsonPath("$.name").value(createRoleResponseDto.getName()))
			.andExpect(jsonPath("$.description").value(createRoleResponseDto.getDescription()))
			.andExpect(jsonPath("$.icon").value(createRoleResponseDto.getIcon()))
			.andExpect(jsonPath("$.isDefaultRole").value(createRoleResponseDto.getIsDefaultRole()))
			.andExpect(jsonPath("$.isAdmin").value(createRoleResponseDto.getIsAdmin()))
			.andExpect(jsonPath("$.deleted").value(createRoleResponseDto.isDeleted()))
			.andExpect(jsonPath("$.deletedAt").value(createRoleResponseDto.getDeletedAt()));

		verify(roleUseCase).save(roleMapper.toEntity(createRoleDto));
		verify(roleMapper).toResponse(role);

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
			.set(field(PaginatedResponseDto::getTotal), rolesList.size())
			.set(field(PaginatedResponseDto::getPage), searchBodyDto.getPage())
			.set(field(PaginatedResponseDto::getSize), searchBodyDto.getSize())
			.set(field(PaginatedResponseDto::getData), rolesList)
			.create();
		when(roleUseCase.search(searchBodyDto.getSearch(), searchBodyDto.getPage(), searchBodyDto.getSize(),
			searchBodyDto.getSort())).thenReturn(rolesList);

		when(roleMapper.toResponse(any(Role.class))).thenReturn(createRoleResponseDto);

		when(roleMapper.toPaginatedResponse(any(List.class), anyInt(), anyInt(),
			anyInt(), any(LocalDateTime.class)))
			.thenReturn(expected);


		mockMvc.perform(post("/api/roles/search").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(searchBodyDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.total").value(expected.getTotal()))
			.andExpect(jsonPath("$.page").value(expected.getPage()))
			.andExpect(jsonPath("$.size").value(expected.getSize()))
			.andExpect(jsonPath("$.data[0].id").value(createRoleResponseDto.getId()))
			.andExpect(jsonPath("$.data[0].name").value(createRoleResponseDto.getName()))
			.andExpect(jsonPath("$.data[0].description").value(createRoleResponseDto.getDescription()))
			.andExpect(jsonPath("$.data[0].deleted").value(createRoleResponseDto.isDeleted()))
			.andExpect(jsonPath("$.data[0].deletedAt").value(createRoleResponseDto.getDeletedAt()));


		verify(roleUseCase).search(eq(searchBodyDto.getSearch()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()),
			eq(searchBodyDto.getSort()));

		verify(roleMapper).toPaginatedResponse(any(List.class), anyInt(), anyInt(),
			anyInt(), any(LocalDateTime.class));
	}

	@Test
	void findRole() throws Exception {
		when(roleUseCase.findRole(createRoleResponseDto.getId())).thenReturn(rolesList.getFirst());
		when(roleMapper.toResponse(rolesList.getFirst())).thenReturn(createRoleResponseDto);

		mockMvc.perform(get("/api/roles/{id}", createRoleResponseDto.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("id").value(createRoleResponseDto.getId()))
			.andExpect(jsonPath("name").value(createRoleResponseDto.getName()))
			.andExpect(jsonPath("description").value(createRoleResponseDto.getDescription()))
			.andExpect(jsonPath("$.icon").value(createRoleResponseDto.getIcon()))
			.andExpect(jsonPath("$.isDefaultRole").value(createRoleResponseDto.getIsDefaultRole()))
			.andExpect(jsonPath("$.isAdmin").value(createRoleResponseDto.getIsAdmin()))
			.andExpect(jsonPath("deleted").value(createRoleResponseDto.isDeleted()))
			.andExpect(jsonPath("deletedAt").value(createRoleResponseDto.getDeletedAt()));

		verify(roleUseCase).findRole(createRoleResponseDto.getId());
		verify(roleMapper).toResponse(rolesList.getFirst());
	}

	@Test
	void updateRole() throws Exception {
		UpdateRoleDto patchRoleDto = UpdateRoleDto.builder()
			.name(createRoleResponseDto.getName())
			.description(createRoleResponseDto.getDescription())
			.build();


		when(roleMapper.toEntity(any(UpdateRoleDto.class))).thenReturn(rolesList.getFirst());
		when(roleUseCase.updateRole(any(Role.class))).thenReturn(rolesList.getFirst());
		when(roleMapper.toResponse(rolesList.getFirst())).thenReturn(createRoleResponseDto);

		mockMvc.perform(patch("/api/roles/{id}", createRoleResponseDto.getId())
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(patchRoleDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("id").value(createRoleResponseDto.getId()))
			.andExpect(jsonPath("name").value(createRoleResponseDto.getName()))
			.andExpect(jsonPath("description").value(createRoleResponseDto.getDescription()))
			.andExpect(jsonPath("$.icon").value(createRoleResponseDto.getIcon()))
			.andExpect(jsonPath("$.isDefaultRole").value(createRoleResponseDto.getIsDefaultRole()))
			.andExpect(jsonPath("$.isAdmin").value(createRoleResponseDto.getIsAdmin()))
			.andExpect(jsonPath("deleted").value(createRoleResponseDto.isDeleted()))
			.andExpect(jsonPath("deletedAt").value(createRoleResponseDto.getDeletedAt()));

		verify(roleMapper).toEntity(any(UpdateRoleDto.class));
		verify(roleUseCase).updateRole(any(Role.class));
		verify(roleMapper).toResponse(any(Role.class));
	}

	@Test
	void removeRole() throws Exception {
		DeleteResponseDto response = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();

		mockMvc.perform(delete("/api/roles/{id}", createRoleResponseDto.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.deletedCount").value(response.getDeletedCount()))
			.andExpect(jsonPath("$.acknowledged").value(response.isAcknowledged()));

		verify(roleUseCase).deleteRole(createRoleResponseDto.getId());
	}

	@Test
	void createThrowsHttpMessageNotReadableException() throws Exception {
		Role product = roleMapper.toEntity(createRoleDto);

		when(roleUseCase.save(product)).thenThrow(new HttpMessageNotReadableException("Cannot deserialize value for Json"));

		mockMvc.perform(post("/api/roles")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createRoleDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("Json Error"))
			.andExpect(jsonPath("$.message").value("Cannot deserialize value for Json"));

	}
}