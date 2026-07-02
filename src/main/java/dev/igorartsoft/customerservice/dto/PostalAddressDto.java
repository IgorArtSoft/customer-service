package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.CustomerValidationRules;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PostalAddressDto(

        @NotBlank(message = "{customer.address.line1.required}")
        @Size(
                max = CustomerValidationRules.ADDRESS_LINE_MAX,
                message = "{customer.address.line1.size}"
        )
        String line1,

        @Size(
                max = CustomerValidationRules.ADDRESS_LINE_MAX,
                message = "{customer.address.line2.size}"
        )
        String line2,

        @NotBlank(message = "{customer.address.city.required}")
        @Size(
                max = CustomerValidationRules.CITY_MAX,
                message = "{customer.address.city.size}"
        )
        String city,

        @NotBlank(message = "{customer.address.region.required}")
        @Size(
                max = CustomerValidationRules.REGION_MAX,
                message = "{customer.address.region.size}"
        )
        String region,

        @NotBlank(message = "{customer.address.postalCode.required}")
        @Size(
                max = CustomerValidationRules.POSTAL_CODE_MAX,
                message = "{customer.address.postalCode.size}"
        )
        String postalCode,

        @NotBlank(message = "{customer.address.countryCode.required}")
        @Pattern(
                regexp = CustomerValidationRules.COUNTRY_CODE,
                message = "{customer.address.countryCode.invalid}"
        )
        String countryCode
) {
}