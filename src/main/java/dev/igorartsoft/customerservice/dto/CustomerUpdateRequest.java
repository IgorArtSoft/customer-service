package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.model.CustomerStatus;
import dev.igorartsoft.customerservice.validation.annotation.OptionalPhone;
import dev.igorartsoft.customerservice.validation.annotation.RequiredCustomerStatus;
import dev.igorartsoft.customerservice.validation.annotation.RequiredEmail;
import dev.igorartsoft.customerservice.validation.annotation.RequiredFirstName;
import dev.igorartsoft.customerservice.validation.annotation.RequiredLastName;
import dev.igorartsoft.customerservice.validation.annotation.RequiredPostalAddress;
import jakarta.validation.Valid;

public record CustomerUpdateRequest(

        @RequiredFirstName
        String firstName,

        @RequiredLastName
        String lastName,

        @RequiredEmail
        String email,

        @OptionalPhone
        String phone,

        @RequiredCustomerStatus
        CustomerStatus status,

        @Valid
        @RequiredPostalAddress
        PostalAddressDto address
) {
}