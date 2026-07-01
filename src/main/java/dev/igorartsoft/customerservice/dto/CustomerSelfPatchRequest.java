package dev.igorartsoft.customerservice.dto;

import jakarta.validation.Valid;

public record CustomerSelfPatchRequest(
        String firstName,
        String lastName,
        String phone,

        @Valid
        PostalAddressPatchRequest address
) {
}