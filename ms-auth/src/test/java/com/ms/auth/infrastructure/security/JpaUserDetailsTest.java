package com.ms.auth.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ms.auth.domain.model.User;
import com.ms.auth.domain.ports.output.persistence.UserPersistencePort;
import com.ms.auth.infrastructure.security.service.JpaUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.ms.auth.data.Data.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsTest {

	private @InjectMocks JpaUserDetails jpaUserDetails;
	private @Mock UserPersistencePort userPersistencePort;

	@Test
	void loadUserByUsername() {
		User user = createUser(1L);

		List<GrantedAuthority> authorities = user.getRoles().stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.collect(Collectors.toList());

		org.springframework.security.core.userdetails.User expected =
			new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				true,
				true,
				true,
				true,
				authorities);

		when(userPersistencePort.findByUsername(user.getEmail())).thenReturn(Optional.of(user));

		org.springframework.security.core.userdetails.User actual = (org.springframework.security.core.userdetails.User) jpaUserDetails.
			loadUserByUsername(user.getEmail());

		assertThat(actual).isEqualTo(expected);
		verify(userPersistencePort).findByUsername(user.getEmail());
	}

	@Test
	void loadUserByUsername_UsernameNotFound() {
		User user = createUser(1L);
		when(userPersistencePort.findByUsername(user.getEmail())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> jpaUserDetails.loadUserByUsername(user.getEmail()));
		verify(userPersistencePort).findByUsername(user.getEmail());
	}
}