package dev.igorartsoft.customerservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerPatchRequest(
		
        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,

        @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,

        @Email(message = "Email must be valid")
        @Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
        String email,
        
        @Pattern(
                regexp = "^(?:\\+[1-9][0-9]{7,14}|[0-9]{7,15})$",
                message = "Phone must contain 7-15 digits, optionally starting with + country code, for example +14165551234 or 4165551234"
        )
        String phone,
                
        String status,

        @Valid
        PostalAddressPatchRequest address
) {
}