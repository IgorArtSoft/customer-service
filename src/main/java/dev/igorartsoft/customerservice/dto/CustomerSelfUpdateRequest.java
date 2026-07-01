package dev.igorartsoft.customerservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerSelfUpdateRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        String phone,

        @Valid
        @NotNull(message = "Address is required")
        PostalAddressDto address
) {
}