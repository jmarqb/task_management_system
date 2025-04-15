package com.ms.auth.application.ports.input;

public interface AuthUseCase {

	String login(String email, String password);
}
