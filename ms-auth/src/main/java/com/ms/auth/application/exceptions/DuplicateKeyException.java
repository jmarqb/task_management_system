package com.ms.auth.application.exceptions;

public class DuplicateKeyException extends RuntimeException {
	public DuplicateKeyException(String message) {
		super(message);
	}
}
