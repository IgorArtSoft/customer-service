package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.model.CustomerStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must be less than or equal to 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must be less than or equal to 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be less than or equal to 255 characters")
        String email,

        @Pattern(
                regexp = "^(?:\\+1)?[2-9][0-9]{2}[2-9][0-9]{6}$",
                message = "Phone must be a valid Canadian/US number, for example +14165551234 or 4165551234"
        )
        String phone,

        @NotNull(message = "Status is required")
        CustomerStatus status,

        @Valid
        PostalAddressDto address
) {
}