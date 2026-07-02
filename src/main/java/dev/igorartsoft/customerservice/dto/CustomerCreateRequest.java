package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.validation.annotation.OptionalPhone;
import dev.igorartsoft.customerservice.validation.annotation.RequiredCustomerId;
import dev.igorartsoft.customerservice.validation.annotation.RequiredEmail;
import dev.igorartsoft.customerservice.validation.annotation.RequiredFirstName;
import dev.igorartsoft.customerservice.validation.annotation.RequiredLastName;
import dev.igorartsoft.customerservice.validation.annotation.RequiredPostalAddress;
import jakarta.validation.Valid;

public record CustomerCreateRequest(

        @RequiredCustomerId
        String customerId,

        @RequiredFirstName
        String firstName,

        @RequiredLastName
        String lastName,

        @RequiredEmail
        String email,

        @OptionalPhone
        String phone,

        @Valid
        @RequiredPostalAddress
        PostalAddressDto address
) {
}