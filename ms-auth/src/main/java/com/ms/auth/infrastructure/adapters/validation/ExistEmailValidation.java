package com.ms.auth.infrastructure.adapters.validation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.ms.auth.application.ports.input.UserUseCase;

@Component
public class ExistEmailValidation implements ConstraintValidator<ExistEmail, String> {

	private final UserUseCase userUseCase;

	public ExistEmailValidation(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (userUseCase == null) {
			return true;
		}
		return !userUseCase.existsByEmail(value);
	}
}
