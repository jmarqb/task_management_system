package com.ms.auth.data;


import java.util.ArrayList;
import java.util.List;

import com.ms.auth.domain.model.Role;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateUserDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.SearchBodyDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateRoleResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.CreateUserResponseDto;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.response.UserRole;
import com.ms.auth.infrastructure.adapters.vo.Gender;

public class Data {
	public static CreateRoleDto createRoleAdmin() {
		return CreateRoleDto.builder()
			.name("ADMIN")
			.description("Admin Description")
			.icon("icon")
			.isAdmin(true)
			.isDefaultRole(false)
			.build();
	}

	public static CreateRoleResponseDto getRoleAdmin(Long id) {
		return CreateRoleResponseDto.builder()
			.id(id)
			.name("ADMIN")
			.description("Admin Description")
			.icon("icon")
			.isAdmin(true)
			.isDefaultRole(false)
			.deleted(false)
			.deletedAt(null)
			.build();
	}

	public static CreateRoleDto createRoleUser() {
		return CreateRoleDto.builder()
			.name("USER")
			.description("ROLE_USER")
			.icon("icon")
			.isAdmin(false)
			.isDefaultRole(true)
			.build();
	}

	public static CreateRoleResponseDto getRoleUser(Long id) {
		return CreateRoleResponseDto.builder()
			.id(id)
			.name("USER")
			.description("ROLE_USER")
			.icon("icon")
			.isAdmin(false)
			.isDefaultRole(true)
			.deleted(false)
			.deletedAt(null)
			.build();
	}

	public static CreateRoleResponseDto getUserUpdatedToAdmin(CreateRoleResponseDto roleUser) {
		return getRoleAdmin(roleUser.getId());
	}

	public static Role getRoleUserUpdatedToAdmin(Role roleUser) {
		CreateRoleResponseDto roleAdmin = getRoleAdmin(roleUser.getId());
		return Role.builder()
			.id(roleAdmin.getId())
			.name(roleAdmin.getName())
			.description(roleAdmin.getDescription())
			.icon(roleAdmin.getIcon())
			.isAdmin(roleAdmin.getIsAdmin())
			.isDefaultRole(roleAdmin.getIsDefaultRole())
			.users(roleUser.getUsers())
			.deleted(roleAdmin.isDeleted())
			.deletedAt(null)
			.build();
	}

	public static Role createRole(Long id) {
		CreateRoleResponseDto role = getRoleUser(id);
		List<User> users = new ArrayList<>();
		return Role.builder()
			.id(id)
			.name(role.getName())
			.description(role.getDescription())
			.icon(role.getIcon())
			.isAdmin(role.getIsAdmin())
			.isDefaultRole(role.getIsDefaultRole())
			.users(users)
			.deleted(role.isDeleted())
			.deletedAt(null)
			.build();
	}

	public static SearchBodyDto createSearchBodyDto(String search, int page, int size, String sort) {
		return SearchBodyDto.builder()
			.search(search)
			.page(page)
			.size(size)
			.sort(sort)
			.build();
	}

	public static User createUser(Long id) {
		User user = User.builder()
			.id(id)
			.firstName("Test")
			.lastName("User")
			.email("test-user@example.com")
			.age(30)
			.password("test-password")
			.phone("+1000000000")
			.gender("MALE")
			.country("Testland")
			.roles(new ArrayList<>()).build();
		user.getRoles().add(createRole(id));
		return user;
	}

	public static CreateUserDto createUserDto() {
		return CreateUserDto.builder()
			.firstName("Test")
			.lastName("User")
			.email("testuser@example.com")
			.age(30)
			.password("testpassword")
			.phone("+100000001")
			.gender("MALE")
			.country("Testland")
			.build();
	}

	public static CreateUserResponseDto createAdminUserResponseDto(Long id) {
		return CreateUserResponseDto.builder()
			.id(id)
			.firstName("firstNameAdmin")
			.lastName("lastNameAdmin")
			.email("testadmin@example.com")
			.age(30)
			.phone("+1234567890")
			.gender(Gender.MALE)
			.country("Testland")
			.roles(new ArrayList<>())
			.build();
	}

	public static CreateUserResponseDto createUserResponseDto(Long id) {
		return CreateUserResponseDto.builder()
			.id(id)
			.firstName("Test")
			.lastName("User")
			.email("testuser@example.com")
			.age(30)
			.phone("+100000000")
			.gender(Gender.MALE)
			.country("Testland")
			.roles(new ArrayList<>())
			.build();
	}

	public static UserRole getUserRole(Long id) {
		CreateRoleResponseDto role = getRoleUser(id);
		return UserRole.builder()
			.name(role.getName())
			.description(role.getDescription())
			.icon(null)
			.isAdmin(role.getIsAdmin())
			.isDefaultRole(role.getIsDefaultRole())
			.build();
	}
}
