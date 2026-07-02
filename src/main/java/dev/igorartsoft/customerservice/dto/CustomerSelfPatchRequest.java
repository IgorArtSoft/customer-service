package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.annotation.OptionalFirstName;
import dev.igorartsoft.customerservice.validation.annotation.OptionalLastName;
import dev.igorartsoft.customerservice.validation.annotation.OptionalPhone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;

public record CustomerSelfPatchRequest(

        @OptionalFirstName
        String firstName,

        @OptionalLastName
        String lastName,

        @OptionalPhone
        String phone,

        @Valid
        PostalAddressPatchRequest address
) {
    @AssertTrue(message = "{customer.patch.empty}")
    public boolean hasAtLeastOneField() {
        return firstName != null
                || lastName != null
                || phone != null
                || address != null;
    }
}