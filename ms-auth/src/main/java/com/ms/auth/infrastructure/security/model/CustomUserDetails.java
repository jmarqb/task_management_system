package com.ms.auth.infrastructure.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

import lombok.Getter;

public class CustomUserDetails extends User {

	@Getter
	private final Long id;

	public CustomUserDetails(Long id, String username, String password, boolean enabled, boolean accountNonExpired,
													boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
	}
}
