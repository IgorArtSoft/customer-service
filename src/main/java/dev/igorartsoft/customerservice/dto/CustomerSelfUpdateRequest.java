package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.CustomerValidationRules;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerSelfUpdateRequest(

        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,

        @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,

        @Pattern(
                regexp = CustomerValidationRules.PHONE,
                message = "Phone must contain 7-15 digits, optionally starting with + country code, for example +14165551234 or 4165551234"
        )
        String phone,

        @Valid
        @NotNull(message = "Address is required")
        PostalAddressDto address
) {
}