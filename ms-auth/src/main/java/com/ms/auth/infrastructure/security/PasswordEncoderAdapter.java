package com.ms.auth.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.ms.auth.domain.ports.output.security.PasswordEncoderProvider;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoderProvider {
	private final PasswordEncoder delegate;

	@Override
	public String encode(CharSequence rawPassword) {
		return delegate.encode(rawPassword);
	}
}
