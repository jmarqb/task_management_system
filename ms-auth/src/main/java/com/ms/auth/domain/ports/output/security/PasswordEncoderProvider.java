package com.ms.auth.domain.ports.output.security;

public interface PasswordEncoderProvider {

	String encode(CharSequence rawPassword);
}
