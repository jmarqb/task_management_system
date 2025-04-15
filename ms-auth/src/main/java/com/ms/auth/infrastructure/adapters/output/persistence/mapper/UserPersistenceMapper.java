package com.ms.auth.infrastructure.adapters.output.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import com.ms.auth.domain.model.User;
import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RolePersistenceMapper.class)
public interface UserPersistenceMapper {


	UserEntity toEntity(User user);

	@Mapping(target = "roles", ignore = true)
	User toDomain(UserEntity userEntity);

	List<UserEntity> toUserEntityList(List<User> users);

	List<User> toUserList(List<UserEntity> userEntities);

	default User toDomainWithRoles(UserEntity userEntity, RolePersistenceMapper roleMapper) {
		User user = toDomain(userEntity);
		if (userEntity.getRoles() != null) {
			user.setRoles(roleMapper.toRoleList(userEntity.getRoles()));
		}
		return user;
	}

	default List<User> toUserListWithRoles(List<UserEntity> userEntities, RolePersistenceMapper roleMapper) {
		List<User> userList = new ArrayList<>();
		for (UserEntity userEntity : userEntities) {
			userList.add(toDomainWithRoles(userEntity, roleMapper));
		}
		return userList;
	}
}
