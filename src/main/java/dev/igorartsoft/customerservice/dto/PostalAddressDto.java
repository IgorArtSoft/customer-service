package dev.igorartsoft.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PostalAddressDto(
        @NotBlank(message = "Address line 1 is required")
        String line1,

        String line2,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Region/state/province is required")
        String region,

        @NotBlank(message = "Postal code is required")
        String postalCode,

        @NotBlank(message = "Country code is required")
        @Pattern(
                regexp = "^[A-Z]{2}$",
                message = "Country code must be a 2-letter uppercase code, for example CA or US"
        )
        String countryCode
) {
}