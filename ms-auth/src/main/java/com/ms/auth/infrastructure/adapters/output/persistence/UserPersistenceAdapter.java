package com.ms.auth.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.ms.auth.domain.model.Pagination;
import com.ms.auth.domain.model.User;
import com.ms.auth.domain.ports.output.persistence.UserPersistencePort;
import com.ms.auth.infrastructure.adapters.output.persistence.mapper.RolePersistenceMapper;
import com.ms.auth.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

	private final UserRepository userRepository;

	private final UserPersistenceMapper userPersistenceMapper;

	private final RolePersistenceMapper rolePersistenceMapper;

	@Transactional(readOnly = true)
	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsByPhone(String phone) {
		return userRepository.existsByPhone(phone);
	}

	@Transactional
	@Override
	public User save(User user) {
		UserEntity userEntity = userPersistenceMapper.toEntity(user);
		UserEntity savedUserEntity = userRepository.save(userEntity);
		return userPersistenceMapper.toDomainWithRoles(savedUserEntity, rolePersistenceMapper);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email).map(userEntity -> userPersistenceMapper.toDomainWithRoles(userEntity,
			rolePersistenceMapper));
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> findAllById(List<Long> ids) {
		return userPersistenceMapper.toUserListWithRoles(userRepository.findAllById(ids), rolePersistenceMapper);
	}

	@Transactional
	@Override
	public List<User> saveAll(List<User> users) {
		List<UserEntity> userEntities = userPersistenceMapper.toUserEntityList(users);
		List<UserEntity> savedUserEntities = userRepository.saveAll(userEntities);
		return userPersistenceMapper.toUserListWithRoles(savedUserEntities, rolePersistenceMapper);
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> searchAll(Pagination pagination) {
		return userPersistenceMapper.toUserListWithRoles(userRepository.searchAll(buildPageable(pagination)),
			rolePersistenceMapper);
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> searchAllByRegex(String regex, Pagination pagination) {
		return userPersistenceMapper.toUserListWithRoles(userRepository.searchAllByRegex(regex, buildPageable(pagination)),
			rolePersistenceMapper);
	}

	@Transactional(readOnly = true)
	@Override
	public User findByIdAndDeletedFalse(Long id) {
		Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedFalse(id);
		return userEntity.map(userEntity1 -> userPersistenceMapper.toDomainWithRoles(userEntity1, rolePersistenceMapper))
			.orElse(null);

	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username).map(userEntity -> userPersistenceMapper.toDomainWithRoles(userEntity,
			rolePersistenceMapper));
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findByEmailWithRoles(String email) {
		return userRepository.findByEmailWithRoles(email).map(userEntity -> userPersistenceMapper.toDomainWithRoles(userEntity,
			rolePersistenceMapper));
	}

	private Pageable buildPageable(Pagination pagination) {
		return PageRequest.of(pagination.page(), pagination.size(), "asc".equalsIgnoreCase(pagination.sort()) ?
			Sort.Direction.ASC : Sort.Direction.DESC, pagination.sortBy());
	}
}
