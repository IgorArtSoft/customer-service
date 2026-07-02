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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Documented
@NotBlank(message = "{customer.id.required}")
@Size(
        min = CustomerValidationRules.CUSTOMER_ID_MIN,
        max = CustomerValidationRules.CUSTOMER_ID_MAX,
        message = "{customer.id.size}"
)
@Pattern(
        regexp = CustomerValidationRules.CUSTOMER_ID,
        message = "{customer.id.invalid}"
)
@Constraint(validatedBy = {})
@Target({ FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface RequiredCustomerId {

    String message() default "{customer.id.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}