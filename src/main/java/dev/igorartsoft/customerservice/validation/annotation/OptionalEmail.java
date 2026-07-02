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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Documented
@Pattern(
        regexp = CustomerValidationRules.NOT_BLANK_IF_PRESENT,
        message = "{customer.email.notBlank}"
)
@Email(message = "{customer.email.invalid}")
@Size(max = CustomerValidationRules.EMAIL_MAX, message = "{customer.email.size}")
@Constraint(validatedBy = {})
@Target({ FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface OptionalEmail {
    String message() default "{customer.email.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}