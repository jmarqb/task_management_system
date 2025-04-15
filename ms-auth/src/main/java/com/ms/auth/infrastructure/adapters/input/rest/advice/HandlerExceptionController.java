package com.ms.auth.infrastructure.adapters.input.rest.advice;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ms.auth.application.exceptions.DuplicateKeyException;
import com.ms.auth.application.exceptions.RoleNotFoundException;
import com.ms.auth.application.exceptions.UserNotFoundException;
import com.ms.auth.domain.model.Error;

@RestControllerAdvice
public class HandlerExceptionController {

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex) {
		List<Error.FieldError> fieldErrors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> Error.FieldError.builder()
				.field(error.getField())
				.rejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null")
				.message(error.getDefaultMessage())
				.build())
			.collect(Collectors.toList());

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.BAD_REQUEST.value())
			.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
			.message("Validation failed")
			.fieldErrors(fieldErrors)
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
	public ResponseEntity<Error> handleDuplicateValidationException(Exception ex) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.BAD_REQUEST.value())
			.error("Duplicate Key")
			.message("Could not execute statement: Duplicate key or Duplicate entry")
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler({RoleNotFoundException.class, UserNotFoundException.class})
	public ResponseEntity<Error> handleValidationException(Exception ex) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.NOT_FOUND.value())
			.error("NOT FOUND")
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseEntity<Error> handleValidationException(HttpMessageNotReadableException ex,
																												WebRequest request) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.BAD_REQUEST.value())
			.error("Json Error")
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler({BadCredentialsException.class})
	public ResponseEntity<Error> handleValidationException(BadCredentialsException ex,
																												WebRequest request) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.UNAUTHORIZED.value())
			.error("Invalid credentials")
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
}
