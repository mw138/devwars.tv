package com.bezman.hibernate.validation.annotation;

import com.bezman.hibernate.validation.validator.AlphaNumericValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AlphaNumericValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AlphaNumeric {

    String message() default "{com.bezman.constraints.alphanumeric}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean spaces() default true;

}
