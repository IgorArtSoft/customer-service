package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.CustomerValidationRules;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PostalAddressPatchRequest(

        @Pattern(
                regexp = CustomerValidationRules.NOT_BLANK_IF_PRESENT,
                message = "{customer.address.line1.notBlank}"
        )
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

        @Pattern(
                regexp = CustomerValidationRules.NOT_BLANK_IF_PRESENT,
                message = "{customer.address.city.notBlank}"
        )
        @Size(
                max = CustomerValidationRules.CITY_MAX,
                message = "{customer.address.city.size}"
        )
        String city,

        @Pattern(
                regexp = CustomerValidationRules.NOT_BLANK_IF_PRESENT,
                message = "{customer.address.region.notBlank}"
        )
        @Size(
                max = CustomerValidationRules.REGION_MAX,
                message = "{customer.address.region.size}"
        )
        String region,

        @Pattern(
                regexp = CustomerValidationRules.NOT_BLANK_IF_PRESENT,
                message = "{customer.address.postalCode.notBlank}"
        )
        @Size(
                max = CustomerValidationRules.POSTAL_CODE_MAX,
                message = "{customer.address.postalCode.size}"
        )
        String postalCode,

        @Pattern(
                regexp = CustomerValidationRules.COUNTRY_CODE,
                message = "{customer.address.countryCode.invalid}"
        )
        String countryCode
) {
    @AssertTrue(message = "{customer.address.patch.empty}")
    public boolean hasAtLeastOneField() {
        return line1 != null
                || line2 != null
                || city != null
                || region != null
                || postalCode != null
                || countryCode != null;
    }
}