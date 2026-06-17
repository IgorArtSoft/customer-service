package dev.igorartsoft.customerservice.dto;

public record AddressResponse(
        String line1,
        String line2,
        String city,
        String province,
        String postalCode,
        String country
) {
}