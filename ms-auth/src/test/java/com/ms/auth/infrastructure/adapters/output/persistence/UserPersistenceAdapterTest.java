package com.ms.auth.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.output.persistence.mapper.RolePersistenceMapper;
import com.ms.auth.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

	private @Mock RolePersistenceMapper rolePersistenceMapper;
	private @Mock UserPersistenceMapper userPersistenceMapper;

	private @Mock UserRepository userRepository;

	private @InjectMocks UserPersistenceAdapter userPersistenceAdapter;

	private UserEntity userEntity;

	private User user;

	private Pageable pageable;
	private final Pagination pagination = new Pagination(0, 10, "ASC", "id");

	@BeforeEach
	void setUp() {
		user = Instancio.of(User.class)
			.set(field(User::getId), 1L)
			.set(field(User::getEmail), "email@email.com")
			.set(field(User::getPhone), "phone")
			.set(field(User::getFirstName), "username")
			.set(field(User::getPassword), "password")
			.set(field(User::getRoles), new ArrayList<>())
			.create();

		userEntity = Instancio.of(UserEntity.class)
			.set(field(UserEntity::getId), user.getId())
			.set(field(UserEntity::getEmail), user.getEmail())
			.set(field(UserEntity::getPhone), user.getPhone())
			.set(field(UserEntity::getFirstName), user.getFirstName())
			.set(field(UserEntity::getPassword), user.getPassword())
			.set(field(UserEntity::getRoles), user.getRoles())
			.create();

		pageable = PageRequest.of(pagination.page(), pagination.size(), "asc".equalsIgnoreCase(pagination.sort()) ?
			Sort.Direction.ASC : Sort.Direction.DESC, pagination.sortBy());
	}


	@Test
	void existsByEmail() {
		when(userRepository.existsByEmail("email@email.com")).thenReturn(true);

		boolean result = userPersistenceAdapter.existsByEmail("email@email.com");

		assertThat(result).isTrue();

		verify(userRepository).existsByEmail("email@email.com");
	}

	@Test
	void existsByPhone() {
		when(userRepository.existsByPhone("phone")).thenReturn(true);

		boolean result = userPersistenceAdapter.existsByPhone("phone");

		assertThat(result).isTrue();

		verify(userRepository).existsByPhone("phone");
	}

	@Test
	void save() {
		when(userPersistenceMapper.toEntity(user)).thenReturn(userEntity);
		when(userRepository.save(userEntity)).thenReturn(userEntity);
		when(userPersistenceMapper.toDomainWithRoles(userEntity, rolePersistenceMapper)).thenReturn(user);

		User result = userPersistenceAdapter.save(user);

		assertThat(result).isEqualTo(user);
		verify(userPersistenceMapper).toEntity(user);
		verify(userRepository).save(userEntity);
		verify(userPersistenceMapper).toDomainWithRoles(userEntity, rolePersistenceMapper);
	}

	@Test
	void findByEmail() {
		Optional<UserEntity> user1 = Optional.of(userEntity);
		when(userRepository.findByEmail("email@email.com")).thenReturn(user1);
		when(userPersistenceMapper.toDomainWithRoles(userEntity, rolePersistenceMapper)).thenReturn(user);

		Optional<User> result = userPersistenceAdapter.findByEmail("email@email.com");

		assertThat(result).isEqualTo(Optional.of(user));

		verify(userRepository).findByEmail("email@email.com");
		verify(userPersistenceMapper).toDomainWithRoles(userEntity, rolePersistenceMapper);
	}

	@Test
	void findAllById() {
		List<User> users = new ArrayList<>();
		users.add(user);
		users.add(user);

		List<Long> ids = new ArrayList<>();
		ids.add(user.getId());
		ids.add(user.getId());

		when(userPersistenceMapper.toUserListWithRoles(userRepository.findAllById(ids), rolePersistenceMapper))
			.thenReturn(users);

		List<User> result = userPersistenceAdapter.findAllById(ids);

		assertThat(result).isEqualTo(users);

		verify(userPersistenceMapper).toUserListWithRoles(userRepository.findAllById(ids), rolePersistenceMapper);
	}

	@Test
	void saveAll() {
		List<User> users = new ArrayList<>();
		users.add(user);
		users.add(user);

		List<UserEntity> userEntities = new ArrayList<>();
		userEntities.add(userEntity);
		userEntities.add(userEntity);

		when(userPersistenceMapper.toUserEntityList(users)).thenReturn(userEntities);
		when(userRepository.saveAll(userEntities)).thenReturn(userEntities);
		when(userPersistenceMapper.toUserListWithRoles(userEntities, rolePersistenceMapper)).thenReturn(users);

		List<User> result = userPersistenceAdapter.saveAll(users);

		assertThat(result).isEqualTo(users);

		verify(userPersistenceMapper).toUserEntityList(users);
		verify(userRepository).saveAll(userEntities);
		verify(userPersistenceMapper).toUserListWithRoles(userEntities, rolePersistenceMapper);
	}

	@Test
	void searchAll() {
		List<User> users = new ArrayList<>();
		users.add(user);
		users.add(user);
		when(userPersistenceMapper.toUserListWithRoles(userRepository.searchAll(pageable), rolePersistenceMapper))
			.thenReturn(users);

		List<User> result = userPersistenceAdapter.searchAll(pagination);

		assertThat(result).isEqualTo(users);

		verify(userPersistenceMapper).toUserListWithRoles(userRepository.searchAll(pageable), rolePersistenceMapper);
	}

	@Test
	void searchAllByRegex() {
		List<User> users = new ArrayList<>();
		users.add(user);
		users.add(user);
		when(userPersistenceMapper.toUserListWithRoles(userRepository.searchAllByRegex("name", pageable), rolePersistenceMapper))
			.thenReturn(users);

		List<User> result = userPersistenceAdapter.searchAllByRegex("name", pagination);

		assertThat(result).isEqualTo(users);

		verify(userPersistenceMapper).toUserListWithRoles(userRepository.searchAllByRegex("name", pageable), rolePersistenceMapper);
	}

	@Test
	void findByIdAndDeletedFalse() {
		Optional<UserEntity> user1 = Optional.of(userEntity);

		when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(user1);
		when(userPersistenceMapper.toDomainWithRoles(userEntity, rolePersistenceMapper)).thenReturn(user);

		User result = userPersistenceAdapter.findByIdAndDeletedFalse(1L);

		assertThat(result).isEqualTo(user);

		verify(userRepository).findByIdAndDeletedFalse(1L);
		verify(userPersistenceMapper).toDomainWithRoles(userEntity, rolePersistenceMapper);

	}

	@Test
	void findByUsername() {
		Optional<UserEntity> user1 = Optional.of(userEntity);
		when(userRepository.findByUsername("username")).thenReturn(user1);
		when(userPersistenceMapper.toDomainWithRoles(userEntity, rolePersistenceMapper)).thenReturn(user);

		Optional<User> result = userPersistenceAdapter.findByUsername("username");

		assertThat(result).isEqualTo(Optional.of(user));

		verify(userRepository).findByUsername("username");
		verify(userPersistenceMapper).toDomainWithRoles(userEntity, rolePersistenceMapper);
	}

	@Test
	void findByEmailWithRoles() {
		Optional<UserEntity> user1 = Optional.of(userEntity);
		when(userRepository.findByEmailWithRoles("email@email.com")).thenReturn(user1);
		when(userPersistenceMapper.toDomainWithRoles(userEntity, rolePersistenceMapper)).thenReturn(user);

		Optional<User> result = userPersistenceAdapter.findByEmailWithRoles("email@email.com");

		assertThat(result).isEqualTo(Optional.of(user));

		verify(userRepository).findByEmailWithRoles("email@email.com");
		verify(userPersistenceMapper).toDomainWithRoles(userEntity, rolePersistenceMapper);
	}
}