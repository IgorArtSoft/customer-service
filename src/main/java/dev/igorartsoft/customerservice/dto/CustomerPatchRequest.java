package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.model.CustomerStatus;
import dev.igorartsoft.customerservice.validation.annotation.OptionalEmail;
import dev.igorartsoft.customerservice.validation.annotation.OptionalFirstName;
import dev.igorartsoft.customerservice.validation.annotation.OptionalLastName;
import dev.igorartsoft.customerservice.validation.annotation.OptionalPhone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;

public record CustomerPatchRequest(

        @OptionalFirstName
        String firstName,

        @OptionalLastName
        String lastName,

        @OptionalEmail
        String email,

        @OptionalPhone
        String phone,

        CustomerStatus status,

        @Valid
        PostalAddressPatchRequest address
) {
    @AssertTrue(message = "{customer.patch.empty}")
    public boolean hasAtLeastOneField() {
        return firstName != null
                || lastName != null
                || email != null
                || phone != null
                || status != null
                || address != null;
    }
}