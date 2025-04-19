package com.jmarqb.ms.project.core.application.exceptions;

public class TaskNotFoundException extends RuntimeException {
	public TaskNotFoundException(String message) {
		super(message);
	}
}
