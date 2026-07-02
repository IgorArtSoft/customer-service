package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.annotation.OptionalPhone;
import dev.igorartsoft.customerservice.validation.annotation.RequiredFirstName;
import dev.igorartsoft.customerservice.validation.annotation.RequiredLastName;
import dev.igorartsoft.customerservice.validation.annotation.RequiredPostalAddress;
import jakarta.validation.Valid;

public record CustomerSelfUpdateRequest(

        @RequiredFirstName
        String firstName,

        @RequiredLastName
        String lastName,

        @OptionalPhone
        String phone,

        @Valid
        @RequiredPostalAddress
        PostalAddressDto address
) {
}