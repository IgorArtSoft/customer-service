package dev.igorartsoft.customerservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        String phone,

        @NotBlank(message = "Status is required")
        String status,

        @Valid
        PostalAddressDto address
) {
}