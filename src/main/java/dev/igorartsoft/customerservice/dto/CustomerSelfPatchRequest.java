package dev.igorartsoft.customerservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record CustomerSelfPatchRequest(
        String firstName,
        String lastName,
        
        @Pattern(
                regexp = CustomerValidationPatterns.PHONE,
                message = "Phone must contain 7-15 digits, optionally starting with + country code, for example +14165551234 or 4165551234"
        )
        String phone,

        @Valid
        PostalAddressPatchRequest address
) {
}