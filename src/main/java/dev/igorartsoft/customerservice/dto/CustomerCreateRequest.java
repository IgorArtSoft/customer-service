package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.CustomerValidationRules;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerCreateRequest(
		
        @NotBlank(message = "Customer id is required")
        @Size(min = 3, max = 64, message = "Customer id must be between 3 and 64 characters")
        @Pattern(
                regexp = CustomerValidationRules.CUSTOMER_ID,
                message = "Customer id may contain only letters, numbers, underscore, or hyphen"
        )
        String customerId,
        
		@NotBlank
        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,

        @NotBlank
        @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,

        @NotBlank
        @Email(message = "Email must be valid")
        @Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
        String email,
        
        @Pattern(
                regexp = CustomerValidationRules.PHONE,
                message = "Phone must contain 7-15 digits, optionally starting with + country code, for example +14165551234 or 4165551234"
        )
        String phone,

        @Valid
        PostalAddressDto address
) {
}