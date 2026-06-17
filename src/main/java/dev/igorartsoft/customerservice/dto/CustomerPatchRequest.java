package dev.igorartsoft.customerservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

public record CustomerPatchRequest(
        @Email(message = "Email must be valid")
        String email,

        String firstName,
        String lastName,
        String phone,
        String status,

        @Valid
        AddressRequest address
) {
}