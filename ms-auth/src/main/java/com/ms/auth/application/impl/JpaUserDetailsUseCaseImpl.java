package com.ms.auth.application.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import com.ms.auth.domain.model.CustomUserDetails;
import com.ms.auth.domain.model.User;
import com.ms.auth.domain.ports.output.persistence.UserPersistencePort;

@Component
@RequiredArgsConstructor
@Transactional
public class JpaUserDetailsUseCaseImpl implements UserDetailsService {

	private final UserPersistencePort userPersistencePort;

	@Override
	public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> userOptional = userPersistencePort.findByUsername(email);
		if (userOptional.isEmpty()) {
			throw new UsernameNotFoundException("Username %s not exists!".formatted(email));
		}
		User user = userOptional.orElseThrow();
		List<GrantedAuthority> authorities = user.getRoles().stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.collect(Collectors.toList());

		return new CustomUserDetails(
			user.getId(),
			user.getEmail(),
			user.getPassword(),
			true,
			true,
			true,
			true,
			authorities);
	}
}
