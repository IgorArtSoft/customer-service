package dev.igorartsoft.customerservice.dto;

import java.time.Instant;

public record CustomerResponse(
        String customerId,
        String email,
        String firstName,
        String lastName,
        String phone,
        String status,
        PostalAddressDto address,
        Instant createdAt,
        Instant updatedAt
) {
}