package com.ms.auth.application.exceptions;

public class RoleNotFoundException extends RuntimeException {
	public RoleNotFoundException(String message) {
		super(message);
	}
}
