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

@Documented
@Pattern(
        regexp = CustomerValidationRules.PHONE,
        message = "{customer.phone.invalid}"
)
@Constraint(validatedBy = {})
@Target({ FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface OptionalPhone {
    String message() default "{customer.phone.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}