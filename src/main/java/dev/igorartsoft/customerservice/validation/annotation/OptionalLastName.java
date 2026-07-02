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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Documented
@Pattern(
        regexp = CustomerValidationRules.NOT_BLANK_IF_PRESENT,
        message = "{customer.lastName.notBlank}"
)
@Size(max = CustomerValidationRules.NAME_MAX, message = "{customer.lastName.size}")
@Constraint(validatedBy = {})
@Target({ FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface OptionalLastName {
    String message() default "{customer.lastName.notBlank}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}