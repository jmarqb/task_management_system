package com.ms.auth.infrastructure.adapters.output.persistence.mapper;

import java.util.List;

import com.ms.auth.domain.model.Role;
import com.ms.auth.infrastructure.adapters.output.persistence.model.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolePersistenceMapper {


	RoleEntity toEntity(Role role);

	@Mapping(target = "users", ignore = true)
	Role toDomain(RoleEntity roleEntity);


	List<Role> toRoleList(List<RoleEntity> roleEntities);

}
