package com.ms.auth.infrastructure.adapters.output.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

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

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByPhone(String phone) {
		return userRepository.existsByPhone(phone);
	}

	@Override
	public User save(User user) {
		UserEntity userEntity = userPersistenceMapper.toEntity(user);
		UserEntity savedUserEntity = userRepository.save(userEntity);
		return userPersistenceMapper.toDomainWithRoles(savedUserEntity, rolePersistenceMapper);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email).map(userEntity -> userPersistenceMapper.toDomainWithRoles(userEntity,
			rolePersistenceMapper));
	}

	@Override
	public List<User> findAllById(List<Long> ids) {
		return userPersistenceMapper.toUserListWithRoles(userRepository.findAllById(ids), rolePersistenceMapper);
	}

	@Override
	public List<User> saveAll(List<User> users) {
		List<UserEntity> userEntities = userPersistenceMapper.toUserEntityList(users);
		List<UserEntity> savedUserEntities = userRepository.saveAll(userEntities);
		return userPersistenceMapper.toUserListWithRoles(savedUserEntities, rolePersistenceMapper);
	}

	@Override
	public List<User> searchAll(Pageable pageable) {
		return userPersistenceMapper.toUserListWithRoles(userRepository.searchAll(pageable), rolePersistenceMapper);
	}

	@Override
	public List<User> searchAllByRegex(String regex, Pageable pageable) {
		return userPersistenceMapper.toUserListWithRoles(userRepository.searchAllByRegex(regex, pageable), rolePersistenceMapper);
	}

	@Override
	public User findByIdAndDeletedFalse(Long id) {
		Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedFalse(id);
		return userEntity.map(userEntity1 -> userPersistenceMapper.toDomainWithRoles(userEntity1, rolePersistenceMapper))
			.orElse(null);

	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username).map(userEntity -> userPersistenceMapper.toDomainWithRoles(userEntity,
			rolePersistenceMapper));
	}

	@Override
	public Optional<User> findByEmailWithRoles(String email) {
		return userRepository.findByEmailWithRoles(email).map(userEntity -> userPersistenceMapper.toDomainWithRoles(userEntity,
			rolePersistenceMapper));
	}
}
