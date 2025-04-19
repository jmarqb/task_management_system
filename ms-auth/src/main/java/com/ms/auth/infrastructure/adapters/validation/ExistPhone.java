package com.ms.auth.infrastructure.adapters.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExistPhoneValidation.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistPhone {
	String message() default "the Phone number already exists";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
