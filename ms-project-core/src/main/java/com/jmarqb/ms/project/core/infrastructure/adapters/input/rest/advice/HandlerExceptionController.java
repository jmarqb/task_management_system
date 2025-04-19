package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.advice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import com.jmarqb.ms.project.core.application.exceptions.*;
import com.jmarqb.ms.project.core.domain.model.Error;
import feign.FeignException;

@Slf4j
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

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<Error> handleFeignException(FeignException ex) {
		HttpStatus status = HttpStatus.BAD_GATEWAY;
		String message = "Error calling external service";

		try {
			String body = ex.contentUTF8();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(body);

			if (ex.status() == 404) {
				status = HttpStatus.NOT_FOUND;
				message = json.has("message") ? json.get("message").asText() : "User not found";
			} else if (ex.status() == 500) {
				status = HttpStatus.BAD_GATEWAY;
				message = "The auth server is unavailable";
			} else {
				message = json.has("message") ? json.get("message").asText() : "Unknown error from external service";
			}

		} catch (Exception parsingException) {
			message = "Error calling external service - Unexpected error";
		}

		Error response = Error.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(message)
				.build();

		return ResponseEntity.status(status).body(response);
	}


	@ExceptionHandler({HttpClientErrorException.Unauthorized.class, AccessDeniedException.class})
	public ResponseEntity<Error> handleUnauthorizedValidationException(Exception ex) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.UNAUTHORIZED.value())
			.error("Unauthorized")
			.message("Unauthorized")
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler({UnauthorizedProjectException.class, UnauthorizedTaskAccessException.class})
	public ResponseEntity<Error> handleUnauthorizedExceptions(Exception ex) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.UNAUTHORIZED.value())
			.error("Unauthorized Access")
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler({InvalidProjectForUserException.class, ArchivedProjectException.class})
	public ResponseEntity<Error> handleInvalidProjectForUserException(Exception ex) {

		Error response = Error.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.BAD_REQUEST.value())
			.error("Invalid Project for User")
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler({ProjectNotFoundException.class, TaskNotFoundException.class, ProjectUserNotFoundException.class})
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
}
