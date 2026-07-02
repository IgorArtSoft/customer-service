package dev.igorartsoft.customerservice.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dev.igorartsoft.customerservice.validation.CustomerValidationRules;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Documented
@NotBlank(message = "{customer.firstName.required}")
@Size(max = CustomerValidationRules.NAME_MAX, message = "{customer.firstName.size}")
@Constraint(validatedBy = {})
@Target({ FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface RequiredFirstName {
    String message() default "{customer.firstName.required}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}