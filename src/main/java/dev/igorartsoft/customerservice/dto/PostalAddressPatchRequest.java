package dev.igorartsoft.customerservice.dto;

import jakarta.validation.constraints.Pattern;

public record PostalAddressPatchRequest(
        String line1,
        String line2,
        String city,
        String region,
        String postalCode,

        @Pattern(
                regexp = "^[A-Z]{2}$",
                message = "Country code must be a 2-letter uppercase code, for example CA or US"
        )
        String countryCode
) {
}